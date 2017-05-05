xterm -title "A" -e "cd ./../out/production/DistanceVectorSimulator; java Router -reverse ../../../resources/problem2a.txt" &
xterm -title "B" -e "sleep 1; cd ./../out/production/DistanceVectorSimulator; java Router -reverse ../../../resources/problem2b.txt" &
xterm -title "C" -e "sleep 2; cd ./../out/production/DistanceVectorSimulator; java Router -reverse ../../../resources/problem2c.txt" &
xterm -title "D" -e "sleep 3; cd ./../out/production/DistanceVectorSimulator; java Router -reverse ../../../resources/problem2d.txt" &
xterm -title "E" -e "sleep 4; cd ./../out/production/DistanceVectorSimulator; java Router -reverse ../../../resources/problem2e.txt" &
xterm -title "F" -e "sleep 5; cd ./../out/production/DistanceVectorSimulator; java Router -reverse ../../../resources/problem2f.txt; sleep 10;"



