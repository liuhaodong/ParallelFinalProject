# ParallelFinalProject
This is our repo for 15618 final project.

To build the project, run


>gradle build

To Use, first edit master_addresses and worker_addresses to add the ip and port you will be using:


Run Worker


>java -jar 15618.jar worker <port_you_are_using>

Run Master


>java -jar 15618.jar master <port_you_are_using>

Run Client


>java -jar 15618.jar client <your_trace_file>/<request_rate>
