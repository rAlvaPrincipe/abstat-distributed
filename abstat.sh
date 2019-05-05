function start(){
	echo "Starting ABSTAT-Spark"
	cd backend
	java -Xmx512m -Xms256m -jar target/abstat-distributed-backend-0.0.1-SNAPSHOT.jar
}

function build(){
	cd backend
	mvn package --quiet 
	echo "backend builded"
	cd ../summarization-spark
	mvn package --quiet
	echo "spark app builded"
}


relative_path=`dirname $0`
cd $relative_path

case "$1" in
	start)
		start 
    		;;
	build)
		build
		;;
	*)
	echo "Usage: abstat start | build "
		;;
esac
