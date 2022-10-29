package com.example.campus_positioning_system.Activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.example.campus_positioning_system.Map.DrawingAssistant;
import com.example.campus_positioning_system.R;
import com.example.campus_positioning_system.RoomList.QuickDialListViewHolderRoom;
import com.example.campus_positioning_system.RoomList.RoomListConverter;
import com.example.campus_positioning_system.RoomList.RoomListViewHolderBuilding;
import com.example.campus_positioning_system.RoomList.RoomListViewHolderLevel;
import com.example.campus_positioning_system.RoomList.RoomListViewHolderRoom;

import java.util.List;

/**
 * @version 1.0
 */
public class QuickDialActivity extends AppCompatActivity {

    /**
     * List the Rooms will be displayd in
     */
    RecyclerView list;


    /**
     * Creates the room list onCreate of the RoomSelectionActivity
     * @param savedInstanceState State to restore if there is one
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quick_dial_list);
        RoomListConverter.setMyContext(MainActivity.mainContext());

        list = findViewById(R.id.quick_dial_recycler_view);
        list.setLayoutManager(new LinearLayoutManager(this));

        TreeViewHolderFactory factory = (v, layout) -> new QuickDialListViewHolderRoom(v,this);
        TreeViewAdapter listadapter = new TreeViewAdapter(factory);
        list.setAdapter(listadapter);

        listadapter.updateTreeNodes(RoomListConverter.generateQuickDialTreeNodeList(this));
        //DrawingAssistant.setPointsOfInterestsNodes(RoomListConverter.generatePOINodes(this));

        //RoomListConverter.printList(this);

    }


    /**
     * Handles going back to the map when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        //System.out.println("User wants to go back from Room list");
        //System.out.println("Navigating from Room List back to Main");
        finish();
    }


}
