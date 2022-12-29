package com.example.campus_positioning_system.Map;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.util.Pair;

import com.example.campus_positioning_system.Node;

import java.util.ArrayList;
import java.util.List;


public class NextDirectionThread extends Thread {

    private List<Node> path;

    public NextDirectionThread(List<Node> path){
        this.path = path;
    }



    private double calculateDirectionsAngle(Node first, Node second, Node third){
        double phi = 0.0d;
        double factionVector, numeratorVector, denominatorVector = 0.0d;

        Pair<Integer,Integer> vector1 = new Pair<Integer,Integer>((first.getX() - second.getX()), (first.getY() - second.getY()));
        Pair<Integer,Integer> vector2 = new Pair<Integer,Integer>((second.getX() - third.getX()), (second.getY() - third.getY()));


        //Vector calculations for phi
        numeratorVector = (vector1.first * vector2.first) + (vector1.second * vector2.second);

        denominatorVector = (sqrt(((pow(vector1.first, 2) + (pow(vector1.second, 2))))) *
                (sqrt(((pow(vector2.first, 2) + (pow(vector2.second, 2)))))));
        factionVector = numeratorVector/denominatorVector;

        phi = Math.toDegrees(Math.acos(factionVector));





        return phi;
    }
    // 4 möglichkeiten : Rechts Links Hoch Runter
    @Override
    public void run(){
        String direction = "";
        for(int i = 0; i<path.size()-2;i++){
            double angle = calculateDirectionsAngle(path.get(i),path.get(i+1),path.get(i+2));
            angle = angle;
            System.out.println(angle);
            if(angle>45 && angle<135){
                direction = "left";
                break;
            }
            if((angle>225 && angle<315)||(angle>-135 && angle<-45)){
                direction = "right";
                break;
            }
        }

        System.out.println(direction + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx");

    }


}
