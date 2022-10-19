package com.example.campus_positioning_system.LocationNavigation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import com.example.campus_positioning_system.Map.DrawingAssistant;
import com.example.campus_positioning_system.Node;

public class LocationFakerThread extends Thread{

    ArrayList<Node> fakeNodes = new ArrayList<>();
    static Node reValue = new Node();

    @Override
    public void run() {
        fakeNodes.add(new Node("",43,31,1));
        fakeNodes.add(new Node("",55,67,1));
        fakeNodes.add(new Node("",49,61,1));
        fakeNodes.add(new Node("",55,56,1));
        fakeNodes.add(new Node("",48,58,1));
        for(int i = 0; i<1;i++){
            try {
                Thread.sleep(2000);
                reValue = fakeNodes.get(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // DrawingAssistant.drawPath();
    }
    public static Node getNote() {
        return reValue;
    }
}
