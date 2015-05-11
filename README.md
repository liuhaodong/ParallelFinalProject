# ParallelFinalProject
This is our repo for 15618 final project.

To build the project, run


>gradle build

To Use, first edit master_addresses and worker_addresses to add the ip and port you will be using:


Run Worker


>java -jar 15618.jar worker worker_port (eg. java -jar 15618.jar worker 5000)

Run Master


>java -jar 15618.jar master master_port (eg. java -jar 15618.jar master 3000)

Run Client


>java -jar 15618.jar client your_trace_file/request_rate (eg. java -jar 15618.jar client 100)
