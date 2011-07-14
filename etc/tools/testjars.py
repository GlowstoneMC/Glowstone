# testjars.py
#
# Scans all .jar files in plugins/ directory of current
# WD for dependencies on CraftBukkit-only methods and
# classes (org.bukkit.craftbukkit and net.minecraft).
# Prints SAFE if no dependencies are found, or UNSAFE
# if the plugin requires CraftBukkit.
#
# Requires Solum (https://github.com/TkTech/Solum)

from glob import glob
from solum import JarFile, ClassFile, ConstantType
from os import path

def narrow(constant):
    name = constant["class"]["name"]["value"]
    if name.startswith("org/bukkit/craftbukkit"):
        return True

    if name.startswith("net/minecraft"):
        return True

    return False


def examine(buffer_):
    """
    Returns True if the class is considered "Safe",
    else False.
    """
    cf = ClassFile(buffer_, str_as_buffer=True)

    if cf.constants.find(tag=ConstantType.METHOD_REF, f=narrow):
        return False

    if cf.constants.find(tag=ConstantType.FIELD_REF, f=narrow):
        return False

    return True

if __name__ == "__main__":
    total = safe = 0
    for jar in glob("plugins/*.jar"):
        total = total + 1
        plugin = JarFile(jar)
        if False in plugin.map(examine, parallel=False):
            print "UNSAFE  %s" % info(jar, plugin)
        else:
            safe = safe + 1
            print "SAFE    %s" % info(jar, plugin)
    print "  %d in %d plugins were safe (%d%%)" % (safe, total, safe * 100 / total)