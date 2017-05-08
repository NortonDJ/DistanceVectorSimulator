#CS305-Project3: DistanceVectorSimulator
#DATE: April.9th.2017
#AUTHORS: Darren Norton, Yizhong Chen

Introduction: Computer Networking project that simulates the distance vector algorithm through multi-threaded and network programming techniques. 

How to Run This Project:
    1. Open terminal. 
    2. Then Go to “/src” directory, compile the program by  “javac Router.java”
    3. Run the program. 
        (i)With "Poison Reverse", 'java Router <-reverse> <pathToFile>'
        (ii)Without "Poison Reverse", 'java Router <pathToFile>'
        
Example of this would be:
    java Router -reverse ../resources/problem1a.txt
    
Our neighbors files exist in the resources directory along with some shell scripts to simulate the 4 scenarios from project description. The files are called:
    a. problem1.sh
    b. problem1poison.sh
    c. problem2.sh
    d. problem2poison.sh

Run these files by cd to the resources directory and using:
    ./problem1.sh
    ...

The format of the neighbors file is:
<IPAddress> <Port>
<NeighborAddress> <Port> <Distance>

Here is problem1c.txt for example:
127.0.0.1 9878
127.0.0.1 9877 1
127.0.0.1 9879 1


We have four supported commands, PRINT, MSG, CHANGE, and !help. These commands adhere to the project requirements, again copied here for convenience.
    
    a.)PRINT - print the current node's distance vector, and the distance vectors received from the neighbors.
    b.)MSG <dst-ip> <dst-port> <msg> - send message msg to a destination with the specified address.
    c.)CHANGE <dst-ip> <dst-port> <new-weight> - change the weight between the current node and the specified node to new-weight and update the specified node about the change.
    d.)!help -  Prints this menu
