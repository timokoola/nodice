package com.moarub.diceness;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class DicePlayer {

	public static final int SHAKE = R.raw.diceshake;
	public static final int ROLL = R.raw.clatter1;
	private static SoundPool fSoundPool;
	private static HashMap<String, Integer> fSounds;

	public static void initPlayer(Context context) {
		fSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		fSounds = new HashMap<String, Integer>();
		fSounds.put("shake", fSoundPool.load(context, SHAKE, 1));
		fSounds.put("roll", fSoundPool.load(context, ROLL, 1));
	}

	public static int playShake(Context context) {
		if (fSoundPool == null || fSounds == null) {
			initPlayer(context);
		}
		return fSoundPool.play(fSounds.get("shake"), 1, 1, 2, 0, 1.f);
	}

	public static int playRoll(Context context) {
		if (fSoundPool == null || fSounds == null) {
			initPlayer(context);
		}
		return fSoundPool.play(fSounds.get("roll"), 1, 1, 0, 0, 1.f);
	}

	public static void stopPlaying(int streamID) {
		fSoundPool.stop(streamID);
	}

}
