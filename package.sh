cd feudalism
mvn package
mv target/feudalism-0.1.jar deps/shade
cd deps/shade
unzip -qq feudalism-0.1.jar
rm feudalism-0.1.jar
zip -qq -r feudalism-0.1.jar META-INF feudalism plugin.yml ca org
rm META-INF -r
rm feudalism -r
rm plugin.yml
cd ../..
mv deps/shade/feudalism-0.1.jar target