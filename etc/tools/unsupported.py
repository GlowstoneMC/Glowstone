# unsupported.py
#
# scans .java files in src/main/java for
# UnsupportedOperationExceptions and prints
# a summary to standard output.

from glob import glob
from os import path
import re

def rglob(dir, pattern):
    result = []
    for file in glob(dir + "/*"):
        if path.isdir(file):
            result += rglob(file, pattern)
        elif re.match(pattern, file):
            result.append(file)
    return result

for file in rglob("src/main/java", ".+\\.java"):
    cfile = file[len("src/main/java/net/glowstone/"):len(file)-5].replace("/", ".").replace("\\", ".")
    function = 'unknown'
    for line in open(file):
        if re.match(" *public", line):
            function = line.strip()
        if line.find("UnsupportedOperationException") >= 0:
            print cfile + ": " + function
            if line.strip() != 'throw new UnsupportedOperationException("Not supported yet.");':
                print "    " + line.strip()[len('throw new UnsupportedOperationException'):]
            function = 'unknown'
