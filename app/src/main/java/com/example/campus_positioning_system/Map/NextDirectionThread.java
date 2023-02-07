package com.example.campus_positioning_system.Map;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.util.Pair;

import com.example.campus_positioning_system.Fragments.MainFragment;
import com.example.campus_positioning_system.Node;

import java.util.ArrayList;
import java.util.List;


public class NextDirectionThread extends Thread {
    private List<Node> path;

    public NextDirectionThread(List<Node> path){
        this.path = path;
    }

    private double calculateCrossProduct(Node first, Node second, Node third){
        double result = 0d;
        Pair<Integer,Integer> vector1 = new Pair<>((second.getX() - first.getX()), (second.getY() - first.getY()));
        Pair<Integer,Integer> vector2 = new Pair<>((second.getX() - third.getX()), (second.getY() - third.getY()));
        result = vector1.first * vector2.second - vector1.second * vector2.first;
        return result;
    }


    private double calculateDirectionsAngle(Node first, Node second, Node third){
        double phi = 0.0d;
        double factionVector, numeratorVector, denominatorVector = 0.0d;
        Pair<Integer,Integer> vector1 = new Pair<>((second.getX() - first.getX()), (second.getY() - first.getY()));
        Pair<Integer,Integer> vector2 = new Pair<>((second.getX() - third.getX()), (second.getY() - third.getY()));
        //Vector calculations for phi
        numeratorVector = (vector1.first * vector2.first) + (vector1.second * vector2.second);
        denominatorVector = (sqrt(((pow(vector1.first, 2) + (pow(vector1.second, 2))))) *
                (sqrt(((pow(vector2.first, 2) + (pow(vector2.second, 2)))))));
        factionVector = numeratorVector/denominatorVector;
        phi = Math.toDegrees(Math.acos(factionVector));
        // System.out.println("Vector1: " + vector1 + "  Vector2: " + vector2 + "||" + numeratorVector + " | " + denominatorVector + " | " + factionVector + " | phi: " + phi );
        return phi;
    }

    @Override
    public void run(){
        String direction = "";
        int pathm = 3;
        int currentZ = path.get(0).getZ();
        if(path.size()<4){
            pathm = 0;
            MainFragment.setDirection("");
        }
        for(int i = 0; i<path.size()-(path.size()-pathm);i++){
            double angle = calculateDirectionsAngle(path.get(i),path.get(i+1),path.get(i+2));
            if(path.get(i+2).getZ() != currentZ){
                if(path.get(+2).getZ() > currentZ) {
                    direction = "up";
                    MainFragment.setDirection(direction);
                    return;
                }
                if(path.get(i+2).getZ() < currentZ) {
                    direction = "down";
                    MainFragment.setDirection(direction);
                    return;
                }
            }
            if(angle>45 && angle<135){
                if(calculateCrossProduct(path.get(i),path.get(i+1),path.get(i+2)) < 0 ){
                    direction = "right";
                }else{
                    direction = "left";
                }
                MainFragment.setDirection(direction);
                return;
            }
        }
        MainFragment.setDirection("straight");
        return;
    }
}
