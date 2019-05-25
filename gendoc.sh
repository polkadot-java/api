# To run the javadoc, download markdown-doclet
# at https://repo1.maven.org/maven2/ch/raffael/markdown-doclet/markdown-doclet/1.4/markdown-doclet-1.4-all.jar
# change the folder of markdown-doclet-1.4-all.jar in the command line below
# Home of markdown-doclet is at https://github.com/Abnaxos/markdown-doclet

javadoc -doclet ch.raffael.mddoclet.MarkdownDoclet -docletpath /markdown-doclet-1.4-all.jar -d doc -sourcepath packages/src/main/java org.polkadot.api
