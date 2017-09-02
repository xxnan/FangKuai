/*
 * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie k�nnen es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
    ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es n�tzlich sein wird, aber
    OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License f�r weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.xxnan.blockinger.game.db;

public class Score {
	private long id;
	private long score;
	private String playerName;
	
	public Score() {
		
	}

	  public long getId() {
	    return id;
	  }
	
	  public void setId(long id) {
	    this.id = id;
	  }
	
	  public long getScore() {
	    return score;
	  }
	
	  public String getScoreString() {
	    return String.valueOf(score);
	  }
	
	  public void setScore(long comment) {
	    this.score = comment;
	  }
	
	  public String getName() {
	    return playerName;
	  }
	
	  public void setName(String comment) {
	    this.playerName = comment;
	  }
	
	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return  String.valueOf(score) + "@" + playerName;
	  }
}
