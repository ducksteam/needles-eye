package com.ducksteam.needleseye.entity.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.Main;

import static com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK;
import static com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE;
import static com.ducksteam.needleseye.Main.dynamicsWorld;
import static com.ducksteam.needleseye.Main.entities;

/**
 * An invisible object in the world used as a trigger for events to happen.
 * @author SkySourced
 * @see CollisionListener
 */
public class WorldTrigger extends Entity {
    /** Code to run when the trigger is activated */
    private final Entity.EntityRunnable runnable;
    private final btCollisionShape shape;
    private final btCollisionObject colObj;
    private final TriggerType triggerType;
    private boolean enabled = true;

    /**
     * Creates a world trigger
     * @param shape The shape of the collider
     * @param worldTrans The transformation matrix applied to the trigger
     * @param runnable The action to be taken when the trigger is triggered
     * @param flags The bitwise flags that define which entities are eligible to activate the trigger (only one bit has to match)
     * @param triggerType When the trigger will activate
     */
    public WorldTrigger(btCollisionShape shape, Matrix4 worldTrans, Entity.EntityRunnable runnable, int flags, TriggerType triggerType) {
        this.runnable = runnable;

        colObj = new btCollisionObject();
        this.shape = shape;
        colObj.setCollisionShape(shape);
        colObj.setWorldTransform(worldTrans);

        id = Entity.currentId++;
        Main.entities.put(id, this);
        colObj.setUserValue(id);

        colObj.setCollisionFlags(CF_CUSTOM_MATERIAL_CALLBACK | CF_NO_CONTACT_RESPONSE); // the cf custom material callback flag is required for custom collision
        colObj.setContactCallbackFlag(Entity.TRIGGER_GROUP);

        this.flags = flags;
        if ((flags & Entity.TRIGGER_GROUP) == Entity.TRIGGER_GROUP) {
            this.flags -= Entity.TRIGGER_GROUP;
            Gdx.app.error("WorldTrigger", "Trigger group flag was passed to trigger " + id);
        }
        colObj.setContactCallbackFilter(this.flags);

        Main.dynamicsWorld.addCollisionObject(colObj);

        this.triggerType = triggerType;
    }

    /** Called when a matching object has entered the trigger */
    public void activate(Entity activatingEntity) {
        if ((triggerType == TriggerType.ONCE_ON_ENTER || triggerType == TriggerType.ON_ENTER) && enabled) {
            if (triggerType == TriggerType.ONCE_ON_ENTER) enabled = false;
            runnable.run(activatingEntity);
        }
    }

    /** Called when a matching object has left the trigger */
    public void deactivate(Entity deactivatingEntity) {
        if ((triggerType == TriggerType.ONCE_ON_EXIT || triggerType == TriggerType.ON_EXIT) && enabled) {
            if (triggerType == TriggerType.ONCE_ON_EXIT) enabled = false;
            runnable.run(deactivatingEntity);
        }
    }

    /** Dispose all the bullet objects */
    public void destroy() {
        dynamicsWorld.removeCollisionObject(colObj);
        shape.dispose();
        colObj.dispose();
        entities.remove(id);
    }

    @Override
    public String getModelAddress() {
        return null;
    }

    /**
     * An enum to represent when a trigger should activate
     * @author SkySourced
     */
    public enum TriggerType {
        /** The trigger activates every time a matching entity enters it */
        ON_ENTER,
        /** The trigger activates every time a matching entity exits it */
        ON_EXIT,
        /** The trigger activates once when a matching entity first enters it */
        ONCE_ON_ENTER,
        /** The trigger activates once when a matching entity first exits it */
        ONCE_ON_EXIT
    }
}
