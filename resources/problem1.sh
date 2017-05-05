xterm -title "A" -e "cd ./../out/production/DistanceVectorSimulator; java Router ../../../resources/problem1a.txt" &
xterm -title "B" -e "sleep 1; cd ./../out/production/DistanceVectorSimulator; java Router ../../../resources/problem1b.txt" &
xterm -title "C" -e "sleep 2; cd ./../out/production/DistanceVectorSimulator; java Router ../../../resources/problem1c.txt" &
xterm -title "D" -e "sleep 3; cd ./../out/production/DistanceVectorSimulator; java Router ../../../resources/problem1d.txt" &
xterm -title "E" -e "sleep 4; cd ./../out/production/DistanceVectorSimulator; java Router ../../../resources/problem1e.txt"

