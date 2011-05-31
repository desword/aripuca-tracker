package com.aripuca.tracker;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

public class Track extends AbstractTrack {

	public Track(MyApp myApp) {
		
		super(myApp);

		this.insertNewTrack();
		
	}

	/**
	 * Id of the track being recorded
	 */
	private long trackId;
	public void setTrackId(long tid) {
		this.trackId = tid;
	}
	public long getTrackId() {
		return this.trackId;
	}


	public int getTrackPointsCount() {
		return trackPointsCount;
	}

	/**
	 * Add new track to application db after recording started
	 */
	public void insertNewTrack() {

		ContentValues values = new ContentValues();
		values.put("title", "New track");
		values.put("recording", 1);
		values.put("start_time", this.trackTimeStart);

		try {
			long newTrackId = myApp.getDatabase().insertOrThrow("tracks", null, values);
			this.setTrackId(newTrackId);
		} catch (SQLiteException e) {
			Toast.makeText(myApp.getMainActivity(), "SQLiteException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.w(Constants.TAG, "SQLiteException: " + e.getMessage(), e);
		}

	}

	/**
	 * Update track data after recording finished
	 */
	protected void updateNewTrack() {

		long finishTime = (new Date()).getTime();

		String trackTitle = (new SimpleDateFormat("yyyy-MM-dd H:mm")).format(this.trackTimeStart) + "-" +
								(new SimpleDateFormat("H:mm")).format(finishTime);

		ContentValues values = new ContentValues();
		values.put("title", trackTitle);
		values.put("distance", this.getDistance());
		values.put("total_time", this.getTotalTime());
		values.put("moving_time", this.getMovingTime());
		values.put("max_speed", this.getMaxSpeed());
		values.put("max_elevation", this.getMaxElevation());
		values.put("min_elevation", this.getMinElevation());
		values.put("elevation_gain", this.getElevationGain());
		values.put("elevation_loss", this.getElevationLoss());
		values.put("finish_time", finishTime);
		values.put("recording", 0);

		try {
			myApp.getDatabase().update("tracks", values, "_id=?", new String[] { String.valueOf(this.getTrackId()) });
		} catch (SQLiteException e) {
			Toast.makeText(myApp.getMainActivity(), "SQLiteException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e(Constants.TAG, "SQLiteException: " + e.getMessage(), e);
		}

	}

	/**
	 * Record one track point
	 * 
	 * @param location Current location
	 */
	protected void recordTrackPoint(Location location, int segmentId) {

		ContentValues values = new ContentValues();
		values.put("track_id", this.getTrackId());
		values.put("lat", location.getLatitude());
		values.put("lng", location.getLongitude());
		values.put("elevation", location.getAltitude());
		values.put("speed", location.getSpeed());
		values.put("time", (new Date()).getTime());
		values.put("segment_id", segmentId);
		values.put("distance", this.distance);
		values.put("accuracy", location.getAccuracy());

		try {

			myApp.getDatabase().insertOrThrow("track_points", null, values);

//			this.lastRecordedLocation = location;

			this.trackPointsCount++;

		} catch (SQLiteException e) {
			Toast.makeText(myApp.getMainActivity(), "SQLiteException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e(Constants.TAG, "SQLiteException: " + e.getMessage(), e);
		}

	}

}
