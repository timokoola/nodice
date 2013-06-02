package com.moarub.diceness;

import java.util.Random;

public class Die {
	private int modifier;
	private int multiplier;
	private int type;
	private int fLastRoll = Integer.MIN_VALUE;

	public Die(int multiplier, int type, int modifier) {
		super();
		this.multiplier = multiplier;
		this.type = type;
		this.modifier = modifier;
	}

	public void roll() {
		Random r = new Random();
		fLastRoll = 0;
		for (int i = 0; i < multiplier; i++) {
			fLastRoll += r.nextInt(type) + 1;
		}
		fLastRoll += modifier;
	}

	public int getLastRoll() {
		return fLastRoll;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Die other = (Die) obj;
		if (modifier != other.modifier)
			return false;
		if (multiplier != other.multiplier)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public int getModifier() {
		return modifier;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + modifier;
		result = prime * result + multiplier;
		result = prime * result + type;
		return result;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Die [multiplier=" + multiplier + ", type=" + type
				+ ", modifier=" + modifier + "]";
	}

	public boolean isRolled() {
		return fLastRoll > Integer.MIN_VALUE;
	}

}
