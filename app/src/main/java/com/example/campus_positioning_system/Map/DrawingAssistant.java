package com.example.campus_positioning_system.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.widget.ImageView;

import com.example.campus_positioning_system.Activitys.MainActivity;
import com.example.campus_positioning_system.Fragments.MainFragment;
import com.example.campus_positioning_system.LocationNavigation.PathfindingControl;
import com.example.campus_positioning_system.Node;
import com.example.campus_positioning_system.R;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DrawingAssistant extends Thread {
    //Mover
    private static Mover dotMover;
    MapPosition position;
    private static MapPosition dotMoverMapPosition;

    private static Node currentPosition;
    private static List<Node> path = new ArrayList<>();
    private static List<Node> POIs = new ArrayList<>();

    private static boolean pathDrawn = false;
    private static boolean POIsSet = false;
    private static int testThrotteling = 0;

    //Height and Width of our View's
    private static int dotHeight;
    private static int dotWidth;
    private static int mapHeight;
    private static int mapWidth;
    private static int displayWidth;
    private static int displayHeight;

    //Set Height and View -> Only needs to be done Once -> is in while(true)
    //because its depending on when the Views got inflated and we need to wait for that to happen
    //so Width and Height is not null
    private boolean setHW = false;
    private static boolean currentPositionChanged = true;

    //View's
    private static TouchImageView mapView = null;
    private static int currentMap;

    private final TouchImageView dotView;

    //Map Converter Node to Px on Screen
    private static MapConverter mapConverter;

    //All Map Bitmaps from Original Maps
    private static final List<Bitmap> allBitmapsOriginal = new LinkedList<>();
    private static final List<Bitmap> allBitmapsForPOI = new LinkedList<>();
    private static List<Bitmap> bitmapsWithPath;

    private final static int MAP_EG = R.drawable.egfancy;
    private final static int MAP_OG1 = R.drawable.og1fancy;
    private final static int MAP_OG2 = R.drawable.og2fancy;
    private final static int MAP_OG3 = R.drawable.og3fancy;

    private final static int[] maps = {R.drawable.egfancy, R.drawable.og1fancy, R.drawable.og2fancy, R.drawable.og3fancy};
    private final static int[] bitmaps = {R.drawable.eg, R.drawable.og1example, R.drawable.og2, R.drawable.og345};

    /**
     *
     * @param dotView
     * @param mapView
     */
    public DrawingAssistant(TouchImageView dotView, TouchImageView mapView) {

        DrawingAssistant.mapView = mapView;
        this.dotView = dotView;

        //first initialization of currentPosition at a default value
        currentPosition = new Node("PointZero", 62, 44, 1);

        //first initialization of dotMoverMapPosition on 0,0 of screen
        dotMoverMapPosition = new MapPosition(0.0f,0.0f);
    }

    /** returns static MapPosition of dotMover
     *
     *
     * @return static MapPosition of dotMover
     */
    public static MapPosition getDotMoverMapPosition() {
        return dotMoverMapPosition;
    }

    /** adds Pair (X,Y) to current coordinates of the dotMover
     *
     * @param toAdd values of X and Y coordinates
     */
    public static synchronized void addToDotMoverMapPosition(Pair<Float, Float> toAdd){
        float one = dotMoverMapPosition.getX();
        float two = dotMoverMapPosition.getY();
        dotMoverMapPosition.setX(one + toAdd.first);
        dotMoverMapPosition.setY(two + toAdd.second);
    }

    /** overrides current value of currentPositionChanged in DrawingAssistant
     *
     * @param bool new value of currentPositionChanged
     */
    public static void setCurrentPositionChanged(boolean bool) {
        currentPositionChanged = bool;
    }

    /** returns static TouchImageView of mapView
     *
     * @return static TouchImageView of MapView
     */
    public static TouchImageView getMapView() {
        return mapView;
    }

    /** changes currentPosition (Node object which is changed by WifiScanner)
     * and changes background-image of floor with the z-coordinate of currentPosition
     *
     * @param newCurrentPosition overrides currentPosition
     */
    public static synchronized void setCurrentPosition(Node newCurrentPosition) {
        System.out.println("Drawing Assistant received current position: " + newCurrentPosition.toString());
        currentPositionChanged = true;
        currentPosition = newCurrentPosition;
        int currentZ = currentPosition.getZ();
        if (!pathDrawn) {
            int mapToSet = maps[currentZ];
            mapView.setImageResource(mapToSet);
            currentMap = mapToSet;
        } else {
            int bitmapToSet = bitmaps[currentZ];
            mapView.setImageBitmap(bitmapsWithPath.get(currentZ));
            currentMap = bitmapToSet;
        }
    }

    /** creates copy of @param pathToDestination in path
     *
     * @param pathToDestination list to copy into path
     */
    public static void setPathToDestination(List<Node> pathToDestination) {
        System.out.println("DrawingAssistant received Path reaching from Point: " + pathToDestination.get(0) + " to: " + pathToDestination.get((pathToDestination.size() - 1)));
        pathDrawn = false;
        path = new ArrayList<>(pathToDestination);
    }

    /** WORK IN PROGRESS
     * - POTENTIAL setter for POINodes
     *
     * - TODO: viable POIs
     *
     * @param POINodes list to copy into POIs
     */
    public static void setPointsOfInterestsNodes(List<Node> POINodes) {
        System.out.println("Drawingassistant recieved POI Nodes");
        POIs = new ArrayList<>(POINodes);
        POIsSet = false;
    }

    /** translates list of nodes 'path' into list  of MapPositions
     *
     * @return translated MapPositions of path
     */
    public static List<MapPosition> getCurrentPathAsMapPositions() {
        Bitmap mutableBitmap = allBitmapsOriginal.get(0);

        float oneX = (float) mutableBitmap.getWidth() / 124f;
        float oneY = (float) mutableBitmap.getHeight() / 88f;

        List<MapPosition> mapPositions = new LinkedList<>();

        for (int i = 0; i < path.size(); i++) {
            Node n = path.get(i);
            MapPosition mapPosition = new MapPosition();
            mapPosition.setX(n.getX() * oneX);
            mapPosition.setY(n.getY() * oneY);
            mapPosition.setZ(n.getZ());
            mapPositions.add(mapPosition);
        }

        return mapPositions;
    }

    /** method used for drawing the path on screen, gets redrawn if pathDrawn is false
     *
     */
    public static void drawPath() {
        pathDrawn = true;

        Paint paintEG = new Paint();
        Paint paintNewFloor = new Paint();
        paintEG.setColor(Color.RED);
        paintEG.setStrokeWidth(10);
        paintNewFloor.setColor(Color.BLUE);
        paintNewFloor.setStrokeWidth(10);


        Bitmap mutableBitmap;

        bitmapsWithPath = new LinkedList<>();

        List<MapPosition> mapPositions = getCurrentPathAsMapPositions();

        for(int i = 0;i<4;i++){
            bitmapsWithPath.add(allBitmapsOriginal.get(i).copy(Bitmap.Config.ARGB_8888, true));
        }

        for (int i = 0; i < mapPositions.size() - 1; i++) {
            mutableBitmap = bitmapsWithPath.get(mapPositions.get(i).getZ());
            Canvas canvas = new Canvas(mutableBitmap);
            if (mapPositions.get(i + 1).getZ() != mapPositions.get(i).getZ()) {
                canvas.drawLine(mapPositions.get(i).getX(), mapPositions.get(i).getY(), mapPositions.get(i + 1).getX(), mapPositions.get(i + 1).getY(), paintEG);
                canvas.drawCircle(mapPositions.get(i).getX(), mapPositions.get(i).getY(), 5f, paintEG);
            } else {
                canvas.drawLine(mapPositions.get(i).getX(), mapPositions.get(i).getY(), mapPositions.get(i + 1).getX(), mapPositions.get(i + 1).getY(), paintNewFloor);
                canvas.drawCircle(mapPositions.get(i).getX(), mapPositions.get(i).getY(), 5f, paintNewFloor);
            }
            if (i == mapPositions.size() - 2) {
                Bitmap ogbitmap = BitmapFactory.decodeResource(MainActivity.mainContext().getResources(), R.drawable.map_finish);
                Bitmap mutmap = ogbitmap.copy(Bitmap.Config.ARGB_8888, true);
                canvas.drawBitmap(mutmap, mapPositions.get(i + 1).getX() - 75, mapPositions.get(i + 1).getY() - 140, paintEG);
            }
        }


        mutableBitmap = bitmapsWithPath.get(currentPosition.getZ());
        mapView.setImageBitmap(mutableBitmap);
        //mapConverter.setMapView(MainFragment.getMapView());

    }

    /*public static void drawPOIs() {
        /*
        POIsSet = true;
        System.out.println("XXXXXXXXSSSSSSSSSSSSSSSSSSSSXXXXXXXXXXXXXXXX");

        Paint paintEG = new Paint();
        Paint paintOG1 = new Paint();
        Paint paintOG2 = new Paint();
        Paint paintOG3 = new Paint();
        Paint paintNewFloor = new Paint();

        Bitmap mutableBitmap = allBitmapsForPOI.get(0);
        float oneX = (float) mutableBitmap.getWidth() / 124f;
        float oneY = (float) mutableBitmap.getHeight() / 88f;


        List<MapPosition> mapPositions = new LinkedList<>();
        for (int i = 0; i < POIs.size(); i++) {
            Node n = POIs.get(i);
            MapPosition mapPosition = new MapPosition();
            mapPosition.setX(n.getX() * oneX);
            mapPosition.setY(n.getY() * oneY);
            mapPosition.setZ(n.getZ());
            mapPositions.add(mapPosition);
            System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLll");
        }


        for (int i = 0; i < mapPositions.size() -1; i++) {
            mutableBitmap = allBitmapsOriginal.get(mapPositions.get(i).getZ());
            Canvas canvas = new Canvas(mutableBitmap);


            String imgName = POIs.get(i).getId();
            int resID = MainActivity.mainContext().getResources().getIdentifier(imgName,"drawable",MainActivity.mainContext().getPackageName());
            Bitmap ogbitmap = BitmapFactory.decodeResource(MainActivity.mainContext().getResources(), resID); // HIER R.id.library_poi
            Bitmap mutmap = ogbitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvas.drawBitmap(mutmap, mapPositions.get(i).getX() - 75, mapPositions.get(i + 1).getY() - 140, paintEG);

        }


        mutableBitmap = allBitmapsOriginal.get(currentPosition.getZ());
        mapView.setImageBitmap(mutableBitmap);
        //mapConverter.setMapView(MainFragment.getMapView());
}
         */





    public static Mover getInstanceMover(){
        return dotMover;
    }



    /** calculates closest node from path to dotMoverPosition
     *
     * @return closest node and its coordinates difference to dotMoverPosition
     */
    public Pair<Node, Integer> getClosestPosition(List<Node> mapOfNodes) {
        Pair<Node, Integer> result = new Pair<>(null,null);

        //highest double value, because any position on the map will be closer
        double mathSafe = 1.7976931348623157E+308;

        //theorem of pythagoras for finding closest MapPosition on Path
        for (Node n: mapOfNodes) {
            MapPosition mapPosition = mapConverter.convertNode(n);
            double difX = dotMover.getX() - mapPosition.getX();
            double difY = dotMover.getY() - mapPosition.getY();
            double math = Math.sqrt(Math.pow(difX,2) + Math.pow(difY,2));
            if (mathSafe > math) {
                mathSafe = math;
                result = new Pair<Node,Integer>(n,(int) mathSafe);
            }
        }
        return result;
    }

    public void updatePathOnWalk() {
        if(!path.isEmpty()){
            PathfindingControl.updateCurrentLocation(getClosestPosition(PathfindingControl.getAllNodesOnFloor(currentPosition.getZ())).first);
            setPathToDestination(PathfindingControl.calculatePath());
            pathDrawn = false;
        }
    }



    public int adjustAngle(int angle) {
        angle = (angle +360);
        angle = angle % 360;
        angle = angle -52;
        if(angle < 0){
            angle = 360 - (Math.abs(angle));
        }
        //System.out.println("adust  " + angle);
        return angle;
    }

    @Override
    public void run() {
        float newX = (float) 0;
        float newY = (float) 0;
        dotMover = new Mover("DotMover", newX, newY);

        dotMover.setView(dotView);
        dotMover.start();

        while (!setHW) {
            if ((mapView.getHeight() != 0.0) && (dotView.getHeight() != 0.0f)) {
                displayHeight = MainActivity.height;
                displayWidth = MainActivity.width;

                mapHeight = mapView.getHeight();
                mapWidth = mapView.getWidth();

                dotHeight = dotView.getHeight();
                dotWidth = dotView.getWidth();

                int navigationBarHeight = MainActivity.navigationBarHeight;
                int statusBarHeight = MainActivity.statusBarHeight;
                System.out.println("Drawing assistant received Device height: " + displayHeight + " and width: " + displayWidth);
                System.out.println("Dot height: " + dotHeight + " and width: " + dotWidth);
                System.out.println("Map height: " + mapHeight + " and width: " + mapWidth);
                System.out.println("Navigation Bar height: " + navigationBarHeight);
                System.out.println("Status Bar height: " + statusBarHeight);

                mapView.setMaxHeight(mapHeight);
                mapView.setMaxWidth(mapWidth);


                mapConverter = new MapConverter(mapHeight, mapWidth, dotHeight, dotWidth, mapView);
                //mapConverter.setMapView(MainFragment.getMapView());

                Bitmap egBitmap = BitmapFactory.decodeResource(MainActivity.mainContext().getResources(), MAP_EG);
                Bitmap mutableBitmapEG = egBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap og1Bitmap = BitmapFactory.decodeResource(MainActivity.mainContext().getResources(), MAP_OG1);
                Bitmap mutableBitmapOG1 = og1Bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap og2Bitmap = BitmapFactory.decodeResource(MainActivity.mainContext().getResources(), MAP_OG2);
                Bitmap mutableBitmapOG2 = og2Bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap og345Bitmap = BitmapFactory.decodeResource(MainActivity.mainContext().getResources(), MAP_OG3);
                Bitmap mutableBitmapOG3 = og345Bitmap.copy(Bitmap.Config.ARGB_8888, true);
                allBitmapsOriginal.add(mutableBitmapEG);
                allBitmapsOriginal.add(mutableBitmapOG1);
                allBitmapsOriginal.add(mutableBitmapOG2);
                allBitmapsOriginal.add(mutableBitmapOG3);
                allBitmapsForPOI.add(mutableBitmapEG);
                allBitmapsForPOI.add(mutableBitmapOG1);
                allBitmapsForPOI.add(mutableBitmapOG2);
                allBitmapsForPOI.add(mutableBitmapOG3);

                setHW = true;
            }
        }

        while (true) {

            dotView.setZoom((float) (2 - mapView.getCurrentZoom()));
            dotView.setRotation(adjustAngle(MainActivity.getAngle()));

            //dotView.setRotation(adjustAngle(MainActivity.getAngle() - 52));
            //System.out.println("Angle is : " + adjustAngle(MainActivity.getAngle()-52));

            if (!path.isEmpty() && !pathDrawn) {
                //mapView.setZoom(1.0f);
                drawPath();
            }
            /*
            if (!POIsSet && !POIs.isEmpty()) {
                drawPOIs();
            }
             */

            /**
             * TODO: DROSSELUNG VON ZEICHNEN
             */
            if (testThrotteling > 20){
                //updatePathOnWalk();
                testThrotteling = 0;
            } else {
                testThrotteling++;
            }


            if(currentPositionChanged) {
                mapView.setZoom(1.0f);
                position = mapConverter.convertNode(currentPosition);
                currentPositionChanged = false;
                dotMoverMapPosition = position;
                dotMover.setNewPosition(dotMoverMapPosition.getX(), dotMoverMapPosition.getY());
                dotMover.animationStart();

            }else {
                position = mapConverter.convertPosition(dotMoverMapPosition);
                dotMover.setNewPosition(position.getX(), position.getY());
                dotMover.animationStart();
            }


            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}