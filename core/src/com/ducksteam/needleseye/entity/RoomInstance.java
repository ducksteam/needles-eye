package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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

    public RoomInstance(RoomTemplate room, Vector3 drawPos, Vector2 roomSpacePos, int rot){
        super(drawPos, new Quaternion().setEulerAngles(0, rot, 0), new ModelInstance(room.getModel()));
        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;
    }

    public RoomInstance(RoomTemplate room, Vector2 roomSpacePos, int rot) {
        super(MapManager.getRoomPos(roomSpacePos).sub(new Vector3(5,0,5)).cpy().add(room.getCentreOffset()), new Quaternion(), (room.getModel() == null) ? null : new ModelInstance(room.getModel()));

        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;
    }

    public RoomInstance(RoomTemplate room, Vector2 pos) {
        this(room, pos, 0);
    }

    public RoomTemplate getRoom() {
        return room;
    }

    public Vector2 getRoomSpacePos() {
        return roomSpacePos;
    }

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
            Gdx.app.error("RoomInstance", "Failed to add enemy to a room", e);
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
