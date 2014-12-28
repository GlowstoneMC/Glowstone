#!/bin/bash

[ "$1" == "false" ] && echo "(PR Fetcher) Not a PR, skipping Glowkit build." && exit 0
PR_ID=$1

# exit at the first failure
set -e

curl_wrapped() {
    if [ -n "$GITHUB_ACCESS_TOKEN" ]; then
        curl -u $GITHUB_ACCESS_TOKEN -s $@
    else
        echo >&2 'Warning: no access token provided - strict rate limits will apply!'
        curl -s $@
    fi
}

# Fetch the body of this PR
pr_body=$(curl_wrapped https://api.github.com/repos/GlowstoneMC/Glowstone/pulls/$PR_ID | jq -r '.body' | sed 's/\r$//')

echo "(PR Fetcher) Glowstone pull request: $PR_ID. Body:"
echo "-------------------------------------------------------------------------"
echo "$pr_body"
echo "-------------------------------------------------------------------------"
echo

# Extract from body
declare -a to_check
declare -a checked

to_check=($(echo $pr_body | egrep -o 'GlowstoneMC/Glowkit(/pull/|#)[0-9]+' | sed -r 's#GlowstoneMC/Glowkit(\#|/pull/)##'))

echo "Checking ${#to_check[@]} links: ${to_check[@]}"

for pr in "${to_check[@]}"; do
    pr_state=$(curl_wrapped https://api.github.com/repos/GlowstoneMC/Glowkit/pulls/$pr | jq -r .state)
    if [ "$pr_state" = 'null' ]; then
        echo "Linked issue #$pr is not a pull request, ignoring."
    elif [ "$pr_state" != 'open' ]; then
        echo "Linked PR #$pr state is $pr_state, ignoring."
    else
        checked+=($pr)
    fi
done

echo "Found ${#checked[@]} pull request links: ${checked[@]}"

# Validate
if [ ${#checked[@]} -eq 0 ]; then
    echo "No PR links found, aborting."
    exit 0
elif [ ${#checked[@]} -ne 1 ]; then
    echo "More than one PR link found, aborting."
    exit 0
fi

glowkit_pr=${checked[0]}

# Create a temp directory to work in
TEMP=$(mktemp -d --tmpdir glowkit-XXXXXXXXXX)

# Fetch the PR from the other repo just like travis-ci does
git clone --depth=50 git://github.com/GlowstoneMC/Glowkit.git $TEMP
pushd $TEMP
git fetch origin +refs/pull/$glowkit_pr/merge:
git checkout -qf FETCH_HEAD

# Actually build it!
mvn clean install -Dmaven.javadoc.skip=true

# Remove all the evidence
popd
echo "Removing $TEMP"
rm -rf $TEMP
