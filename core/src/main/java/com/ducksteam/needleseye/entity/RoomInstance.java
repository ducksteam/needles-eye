package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.map.MapManager;
import com.ducksteam.needleseye.map.RoomTemplate;

import java.util.HashMap;

/**
 * Represents an instance of a room in the world
 * @author SkySourced
 */

public class RoomInstance extends Entity {
    RoomTemplate room; // template for the room
    Vector2 roomSpacePos; // 2d position on room grid
    int rot; //IN DEGREES

    HashMap<Integer, EnemyEntity> enemies = new HashMap<>(); // enemies in the room

    /**
     * Create a new room instance, with a specified draw position. This is typically used for hallways
     * @param room the room template
     * @param drawPos the position to draw the room at
     * @param roomSpacePos the position of the room in room space
     * @param rot the rotation of the room
     */
    public RoomInstance(RoomTemplate room, Vector3 drawPos, Vector2 roomSpacePos, int rot){
        super(drawPos, new Quaternion().setEulerAngles(rot, 0, 0), room.getScene());
        if (rot % 90 != 0) throw new IllegalArgumentException("Rotation must be a multiple of 90 degrees");

        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;
    }

    /**
     * Create a new room instance
     * @param room the room template
     * @param roomSpacePos the position of the room in room space
     * @param rot the rotation of the room
     */
    public RoomInstance(RoomTemplate room, Vector2 roomSpacePos, int rot) {
        super(MapManager.getRoomPos(roomSpacePos).sub(new Vector3(5,0,5)).cpy().add(room.getCentreOffset()), new Quaternion().setEulerAngles(rot, 0, 0), (room.getScene() == null) ? null : room.getScene());
        if (rot % 90 != 0) throw new IllegalArgumentException("Rotation must be a multiple of 90 degrees");

        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;
    }

    /**
     * Create a new room instance
     * @param room the room template
     * @param pos the room space position of the room, in whole numbers
     */
    public RoomInstance(RoomTemplate room, Vector2 pos) {
        this(room, pos, 0);
    }

    /**
     * Get the room template of the room
     * @return the room template
     */
    public RoomTemplate getRoom() {
        return room;
    }

    /**
     * Get the position of the room in room space
     * @return the position of the room
     */
    public Vector2 getRoomSpacePos() {
        return roomSpacePos;
    }

    /**
     * Get the central position of the room in world space
     * @return the central position of the room
     */
    public Vector2 getCentreRoomSpacePos() {
        return roomSpacePos.cpy().sub(0.5f, 0.5f);
    }

    /**
     * Get the rotation of the room
     * @return the rotation of the room
     */
    public int getRot() {
        return rot;
    }

    /**
     * Add an enemy entity to the room list
     * @param enemy the entity to add
     */
    public void addEnemy(EnemyEntity enemy) {
        Gdx.app.debug("Population", "Added " + enemy.getClass().getSimpleName() + " to room " + room.getName() + " at " + enemy.getPosition());
        enemies.put(enemy.id, enemy);
    }

    /**
     * Add an enemy entity to the room list based on its id
     * @param id the id of the entity to add
     */
    public void addEnemy(int id){
        try {
            enemies.put(id, (EnemyEntity) Main.entities.get(id));
        } catch (Exception e) {
            Gdx.app.error("RoomInstance", "Failed to add enemy with id " + id + " to room " + room.getName(), e);
        }
    }

    /**
     * Removes an enemy entity from the room
     * @param enemy the enemy to remove
     */
    public void removeEnemy(Entity enemy) {
        enemies.remove(enemy.id);
    }

    /**
     * Removes an enemy entity from the room based on its id
     * @param id the id of the enemy to remove
     */
    public void removeEnemy(int id) {
        enemies.remove(id);
    }

    /**
     * Returns the map of enemies in the room
     * @return the map of enemies
     */
    public HashMap<Integer, EnemyEntity> getEnemies() {
        return enemies;
    }

    /**
     * Returns a string representation of the room instance
     * @return the room as a string
     */
    @Override
    public String toString() {
        return "RoomInstance{" +
                "isrenderable=" + isRenderable +
                ",room={type=" + room.getType() +
                ", name=" + room.getName() +
                "}, pos=" + roomSpacePos +
                '}';
    }

    /**
     * Returns the address of the model of the template
     * @return the file path to the model
     */
    @Override
    public String getModelAddress() {
        return room.getModelPath();
    }
}
