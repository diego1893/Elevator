package com.monsordi.elevator;

import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String[] ARRAY = {"0","1","2","3","4","5","6","7","8","9","10"};

    private EditText requestedFloorET;
    private FloatingActionButton goToFloorButton;
    private FloatingActionButton openDoorButton;
    private FloatingActionButton closeDoorButton;
    private TextView elevatorIndicator;
    private ProgressBar openingProgressBar;
    private ProgressBar closingProgressBar;
    private Switch closedDoorSwitch;
    private Switch openedDoorSwitch;

    private Spinner floorSelector;
    private TextView aisleIndicator;
    private ProgressBar ascendingProgressBar;
    private ProgressBar descendingProgressBar;
    private FloatingActionButton requestAscendingButton;
    private FloatingActionButton requestDescendingButton;

    private ElevatorController elevatorController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elevatorController = new ElevatorController();
        initializeUI();
    }

    private void initializeUI(){
        requestedFloorET = (EditText) findViewById(R.id.desiredFloor);
        goToFloorButton = (FloatingActionButton) findViewById(R.id.goToFloorButton);
        openDoorButton = (FloatingActionButton) findViewById(R.id.openDoorButton);
        closeDoorButton = (FloatingActionButton) findViewById(R.id.closeDoorButton);
        elevatorIndicator = (TextView) findViewById(R.id.elevatorIndicator);
        openingProgressBar = (ProgressBar) findViewById(R.id.openingProgressBar);
        closingProgressBar = (ProgressBar) findViewById(R.id.closingProgressBar);
        closedDoorSwitch = (Switch) findViewById(R.id.closedDoorSwitch);
        openedDoorSwitch = (Switch) findViewById(R.id.openedDoorSwitch);
        floorSelector = (Spinner) findViewById(R.id.spinner);
        aisleIndicator = (TextView) findViewById(R.id.aisleIndicator);
        ascendingProgressBar = (ProgressBar) findViewById(R.id.ascendingProgressBar);
        descendingProgressBar = (ProgressBar) findViewById(R.id.descendingProgressBar);
        requestAscendingButton = (FloatingActionButton) findViewById(R.id.requestAscendingButton);
        requestDescendingButton = (FloatingActionButton) findViewById(R.id.requestDescendingButton);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ARRAY);
        floorSelector.setAdapter(spinnerAdapter);

        setListeners();
    }

    private void setListeners(){
        goToFloorButton.setOnClickListener(this);
        closeDoorButton.setOnClickListener(this);
        openDoorButton.setOnClickListener(this);
        requestAscendingButton.setOnClickListener(this);
        requestDescendingButton.setOnClickListener(this);
        closedDoorSwitch.setOnClickListener(this);
        openedDoorSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.goToFloorButton:
                int desiredFloor = Integer.parseInt(requestedFloorET.getText().toString());
                if(desiredFloor<=Elevator.floorsNumber)
                    elevatorController.goToFloor(desiredFloor);
                break;

            case R.id.closeDoorButton:
                elevatorController.setCloseDoorPressed(true);
                break;

            case R.id.openDoorButton:
                elevatorController.setOpenDoorPressed(true);
                break;

            case R.id.requestAscendingButton:
                elevatorController.requestAscending();
                break;

            case R.id.requestDescendingButton:
                elevatorController.requestDescending();
                break;

            case R.id.closedDoorSwitch:
                elevatorController.setDoorClosed(closedDoorSwitch.isChecked());
                break;

            case R.id.openedDoorSwitch:
                elevatorController.setDoorOpened(openedDoorSwitch.isChecked());
                break;
        }
        elevatorController.checkState();
    }

    private class ElevatorController extends Elevator{
        @Override
        public void doStopTasks() {
            openingProgressBar.setVisibility(View.INVISIBLE);
            closingProgressBar.setVisibility(View.INVISIBLE);
            ascendingProgressBar.setVisibility(View.INVISIBLE);
            descendingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void doStandByTasks() {
            openingProgressBar.setVisibility(View.INVISIBLE);
            closingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void requestAscending() {
            goToFloor(floorSelector.getSelectedItemPosition());
        }

        @Override
        public void requestDescending() {
            goToFloor(floorSelector.getSelectedItemPosition());
        }

        @Override
        public void ascend() {
            ascendingProgressBar.setVisibility(View.VISIBLE);
            ascendOrDescend(true);
        }

        @Override
        public void descend() {
            descendingProgressBar.setVisibility(View.VISIBLE);
            ascendOrDescend(false);
        }

        @Override
        public void closeDoor() {
            openingProgressBar.setVisibility(View.INVISIBLE);
            closingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void openDoor() {
            closingProgressBar.setVisibility(View.INVISIBLE);
            openingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void goToFloor(int floor) {
            if(elevatorController.getCurrentFloor()>floor)
                elevatorController.getDownFloors().add(floor);
            else
                elevatorController.getUpFloors().add(floor);

        }

        private void ascendOrDescend(final boolean isAscending){
            new AscendingOrDescendingTask(isAscending).execute(elevatorController);
        }

        @Override
        public void printState() {
            elevatorIndicator.setText(String.valueOf(elevatorController.getState()));
        }
    }

    public class AscendingOrDescendingTask extends AsyncTask<ElevatorController,Void,ElevatorController>{
        boolean isDescending;

        public AscendingOrDescendingTask(boolean isDescending){
            this.isDescending = isDescending;
        }

        @Override
        protected ElevatorController doInBackground(ElevatorController... params) {
            int currentFloor = params[0].getCurrentFloor();
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime <= 500){}
            if(isDescending)
                params[0].setCurrentFloor(++currentFloor);
            else
                params[0].setCurrentFloor(--currentFloor);

            return params[0];
        }
    }
}
