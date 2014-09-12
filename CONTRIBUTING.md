Contributing to Glowstone
=========================
[Glowstone](http://glowstone.net) is a lightweight, open source Minecraft server written in Java. For those who wish to contribute, we encourage you to fork the repository and submit pull requests. Below you will find guidelines that will explain this process in further detail.

Quick Guide
-----------
1. Create or find an issue on the [issue tracker](https://github.com/GlowstoneMC/Glowstone/issues).
2. Fork Glowstone if you haven't done so already.
3. Create a branch dedicated to your change.
4. Write code addressing your feature or bug.
5. Commit your change according to the [committing guidelines](#committing-your-changes).
6. Push your branch and submit a pull request.

Getting Started
---------------
* Find an issue to fix or a feature to add.
* Search the issue tracker for your bug report or feature.
* Large changes should always have a separate issue to allow discussion.
  * If your feature or bug incorporates a large change, file a new issue, so the feature and its implementation may be tracked separately. This way, the nature of the issue may be discussed before time is spent addressing the issue. 
* Fork the repository on GitHub.

Making Changes
--------------
* Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Name your branch something relevant to the change you are going to make.
  * To quickly create a topic branch based on master, use `git checkout master` followed by `git checkout -b <name>`. Avoid working directly on the `master` branch.
* Make your best effort to meet our [code style guidelines](https://github.com/GlowstoneMC/Glowstone/wiki/Code-Style).
* Changes should generally be future-proof and not strongly tied to details that may change in a future Minecraft version.
* Large changes should be documented by the appropriate javadocs if applicable.

Committing your changes
-----------------------
* Check for unnecessary whitespace with `git diff --check` before committing.
* Describe your changes in the commit description.
* For a prolonged description, continue on a new line.
* If your change addresses an open issue, include in the first line one of:
  * For a bug-related issue: "Fixes #_NNN_".
  * For a feature request: "Resolves #_NNN_".

Example commit message:
> Changed wgen to treat 128 as world height in all cases (fixes #151).

Submitting Your Changes
-----------------------
* Push your changes to the topic branch in your fork of the repository.
* Submit a pull request to this repository.
  * Be concise and to the point with your pull request title.
  * Explain in detail what your changes are and the motives behind them.
  * If your PR is not finished, but you are looking for review anyway, prefix title with [WIP].
* Await peer review and feedback.
* Revise PR based on feedback.

How to get your pull request accepted
-------------------------------------
* Ensure your change does not solely consist of formatting changes.
* Be concise and to the point in your pull request title.
* Address your changes in detail. Explain why you made each change.
* The code must be your work or you must appropriately credit those whose work you have used.

Additional Resources
--------------------
* [Bug tracker](https://github.com/GlowstoneMC/Glowstone/issues)
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)
* IRC: #glowstone (general) and #glowstonedev (development) on EsperNet
