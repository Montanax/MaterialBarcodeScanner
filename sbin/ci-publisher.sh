branch=$1
event=$2

cnt=`echo $branch | egrep -e "(^master$|^release$|^.*-snapshot$)" | wc -l`

if [ $cnt -eq 1 ] && [ "$event" = "push" ]; then
  ./gradlew artifactoryPublish clean
fi

rm -rf build sdk/build

