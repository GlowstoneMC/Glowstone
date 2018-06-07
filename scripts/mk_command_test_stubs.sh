#!/bin/sh
# Generates test stubs for all the command classes that are lacking tests.
# These stubs will inherit testExecuteFailsWithoutPermission().
TESTS=$(find src/main/java/net/glowstone/command -iname "*Command.java" | sed s/\.java$/Test.java/ | sed s_^src/main_src/test_)
for TEST in ${TESTS}; do
  if [ ! -f ${TEST} ]; then
    SIMPLE_NAME=$(echo ${TEST} | sed 's_^.*/__' | sed 's/\.java$//')
    # Exclude SaveToggleCommand since it's tested by SaveOffCommandTest and SaveOnCommandTest
    if (( 'SaveToggleCommandTest' != ${SIMPLE_NAME} )); then
        PACKAGE=$(echo ${TEST} | sed 's_/[^/]*$__' | sed 's_^src/test/java/__' | sed s_/_._g)
        SUBJECT=$(echo ${SIMPLE_NAME} | sed 's/Test$//') # class under test
        echo "\
package ${PACKAGE};\n\
\n\
import net.glowstone.command.CommandTest;\n\
\n\
public class ${SIMPLE_NAME} extends CommandTest<${SUBJECT}> {\n\
\n\
    // TODO: Add more tests.\n\
\n\
    public ${SIMPLE_NAME}() {\n\
        super(${SUBJECT}::new);\n\
    }\n\
}\n\
" >> ${TEST}
    fi
  fi
done
