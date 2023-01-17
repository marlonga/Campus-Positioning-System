package com.example.campus_positioning_system.Fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.campus_positioning_system.Activitys.MainActivity;
import com.example.campus_positioning_system.LocationNavigation.WifiScanner;
import com.example.campus_positioning_system.Map.DrawingAssistant;
import com.example.campus_positioning_system.R;
import com.ortiz.touchview.TouchImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static boolean onlyOnce = true;

    private static TextView textView_direction;
    private static ImageView image_direction;
    private static View background_direction;
    private static ConstraintLayout layout;
    private static DisplayMetrics displayMetrics;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static View rootViewSave;

    public static TouchImageView getMapView() {
        return rootViewSave.findViewById(R.id.map1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        Also... der grobe Plan ist erstmal hier alles zu starten. Also. Sensor Activity - WifiScanner
        Dann den Drawing Assistant damit zu füttern und ab geht die Party... Hoffentlich.
         */

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootViewSave = rootView;

        if (onlyOnce) {
            TouchImageView mapView = rootView.findViewById(R.id.map1);
            TouchImageView dotView = rootView.findViewById(R.id.dot);

            TextView textView = rootView.findViewById(R.id.stockwerkView);

            textView_direction = rootView.findViewById(R.id.text_direction);
            image_direction = rootView.findViewById(R.id.direction_nextdirection);
            background_direction = rootView.findViewById(R.id.background_nextdirection);
            layout = (ConstraintLayout) background_direction;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            displayMetrics = getResources().getDisplayMetrics();

            View[] allViews = new View[]{mapView, dotView};


            WifiScanner.setStockwerkView(textView);

            WifiScanner wifiScanner = new WifiScanner(MainActivity.mainContext());
            new Thread(wifiScanner).start();

            DrawingAssistant drawingAssistant = new DrawingAssistant(allViews);
            drawingAssistant.start();

            onlyOnce = false;
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    public static void setDirection(String direction) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, displayMetrics);
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, displayMetrics);
        params.height = (int) height;
        params.width = (int) width;
        layout.post(new Runnable() {
            @Override
            public void run() {
                layout.setLayoutParams(params);
            }
        });

        switch (direction) {
            case "right":
                buildDirection("Rechts abbiegen", R.drawable.turn_right);
                break;
            case "left":
                buildDirection("Links abbiegen", R.drawable.turn_left);
                break;
            case "up":
                buildDirection("Treppen hoch", R.drawable.go_upstairs);
                break;
            case "down":
                buildDirection("Treppen runter", R.drawable.go_downstairs);
                break;
            case "straight":
                buildDirection("Gerade aus", R.drawable.go_striaght);
            default:
                break;
        }
    }

    public static void removeDirection() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, displayMetrics);
        params.height = (int) height;
        params.width = (int) width;
        layout.post(new Runnable() {
            @Override
            public void run() {
                layout.setLayoutParams(params);
            }
        });
        textView_direction.post(new Runnable() {
            @Override
            public void run() {
                textView_direction.setVisibility(View.GONE);
            }
        });
        image_direction.post(new Runnable() {
            @Override
            public void run() {
                image_direction.setVisibility(View.GONE);
            }
        });
    }

    private static void buildDirection(String text, int residPicture) {
        textView_direction.post(new Runnable() {
            @Override
            public void run() {
                textView_direction.setText(text);
                textView_direction.setVisibility(View.VISIBLE);
            }
        });
        image_direction.post(new Runnable() {
            @Override
            public void run() {
                image_direction.setImageResource(residPicture);
                image_direction.setVisibility(View.VISIBLE);
            }
        });

    }
}