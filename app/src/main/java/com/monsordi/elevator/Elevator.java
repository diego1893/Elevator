package com.monsordi.elevator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 26/03/18.
 */

public abstract class Elevator {

    private static final int STOP = 0;
    private static final int ASCENDING = 1;
    private static final int DESCENDING = 2;
    private static final int STAND_BY = 3;
    private static final int OPENING_DOOR = 4;
    private static final int CLOSING_DOOR = 5;
    private static final int REMOVING_FLOOR = 6;

    private int currentFloor;
    private boolean doorOpened;
    private boolean doorClosed;
    private boolean openDoorPressed;
    private boolean closeDoorPressed;
    private boolean hasDoorBeenOpened;
    private boolean openingTimeElapsed;
    private List<Integer> upFloors;
    private List<Integer> downFloors;
    private int state;
    public static final int floorsNumber = 10;

    public Elevator() {
        this.currentFloor = 0;
        this.doorOpened = false;
        this.doorClosed = true;
        this.openDoorPressed = false;
        this.closeDoorPressed = false;
        this.hasDoorBeenOpened = false;
        this.openingTimeElapsed = false;
        this.upFloors = new ArrayList<>();
        this.downFloors = new ArrayList<>();
        this.state = STOP;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getState() {
        return state;
    }

    public List<Integer> getUpFloors() {
        return upFloors;
    }

    public List<Integer> getDownFloors() {
        return downFloors;
    }

    public void setCurrentFloor(int currentFloor){
        this.currentFloor = currentFloor;
    }

    public void setOpenDoorPressed(boolean openDoorPressed) {
        this.openDoorPressed = openDoorPressed;
    }

    public void setCloseDoorPressed(boolean closeDoorPressed) {
        this.closeDoorPressed = closeDoorPressed;
    }

    public void setDoorOpened(boolean doorOpened) {
        this.doorOpened = doorOpened;
    }

    public void setDoorClosed(boolean doorClosed) {
        this.doorClosed = doorClosed;
    }

    public void checkState(){
        switch (this.state){
            case STOP:
                if(isARequestingFloor(currentFloor) && !hasDoorBeenOpened){
                    this.state = OPENING_DOOR;
                    openDoor();
                }
                else if(!isARequestingFloor(currentFloor) && upFloors.size() != 0 && isFasterToAscend()){
                    this.state = ASCENDING;
                    ascend();
                }
                else if(!isARequestingFloor(currentFloor) && downFloors.size() != 0 && !isFasterToAscend()) {
                    this.state = DESCENDING;
                    descend();
                }
                break;

            case ASCENDING:
                hasDoorBeenOpened = false;
                if(isARequestingFloor(currentFloor)) {
                    this.state = REMOVING_FLOOR;
                    removeCurrentFloor();
                }
                break;

            case DESCENDING:
                hasDoorBeenOpened = false;
                if(isARequestingFloor(currentFloor)) {
                    this.state = REMOVING_FLOOR;
                    removeCurrentFloor();
                }
                break;

            case STAND_BY:
                hasDoorBeenOpened = true;
                if(upFloors.contains(currentFloor))
                    upFloors.remove(currentFloor);
                if(downFloors.contains(currentFloor))
                    downFloors.remove(currentFloor);
                if(openingTimeElapsed || closeDoorPressed) {
                    this.state = CLOSING_DOOR;
                    closeDoor();
                }
                break;

            case OPENING_DOOR:
                openDoorPressed = false;
                openingTimeElapsed = false;
                if(doorOpened) {
                    this.state = STAND_BY;
                    doStandByTasks();
                    startTime();
                }
                else if(closeDoorPressed) {
                    this.state = CLOSING_DOOR;
                    closeDoor();
                }
                break;

            case CLOSING_DOOR:
                closeDoorPressed = false;
                if(doorClosed) {
                    this.state = STOP;
                    doStopTasks();
                }
                else if(openDoorPressed) {
                    this.state = OPENING_DOOR;
                    openDoor();
                }
                break;

            case REMOVING_FLOOR:

                break;
        }
        printState();
    }

    public boolean isARequestingFloor(int currentFloor){
        for(int floor : upFloors ){
            if(floor == currentFloor)
                return true;
        }

        for(int floor : downFloors ){
            if(floor == currentFloor)
                return true;
        }

        return false;
    }

    public boolean isFasterToAscend(){
        return true;
    }

    public void startTime(){
        openingTimeElapsed = true;
        checkState();
    }

    public void removeCurrentFloor(){
        if(upFloors.contains(currentFloor))
            upFloors.remove(currentFloor);
        if(downFloors.contains(currentFloor))
            downFloors.remove(currentFloor);
        this.state = STOP;
        checkState();
    }

    public abstract void printState();
    public abstract void doStopTasks();
    public abstract void doStandByTasks();
    public abstract void requestAscending();
    public abstract void requestDescending();
    public abstract void ascend();
    public abstract void descend();
    public abstract void closeDoor();
    public abstract void openDoor();
    public abstract void goToFloor(int floor);
}
