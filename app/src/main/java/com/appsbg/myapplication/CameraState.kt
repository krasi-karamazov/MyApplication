package com.appsbg.myapplication

enum class CameraState (state: Int){
    STATE_PREVIEW(0),
    STATE_WAITING_LOCK(1),
    STATE_WAITING_PRECAPTURE(2),
    STATE_WAITING_NON_PRECAPTURE(3),
    STATE_WAITING_PICTURE_TAKEN(4),
    STATE_PICTURE_TAKEN(5)
}