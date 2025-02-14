package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.bullet.WorldTrigger;
import com.ducksteam.needleseye.map.DecoTemplate;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.ducksteam.needleseye.Main.dynamicsWorld;

public class DecoInstance extends Entity {
    public DecoTemplate template;
    public RoomInstance parentRoom;
    public boolean shattered = false;
    private WorldTrigger shatterTrigger;
    public final ArrayList<ShatterNode> shatterNodes = new ArrayList<>();

    /**
     * Creates a new deco
     * @param template the template of the deco being created
     * @param parentRoom the room the deco is placed in
     * @param position the relative position as found in {@link com.ducksteam.needleseye.map.RoomTemplate.DecoTagPosition DecoTagPosition}
     * @param rotation the rotation of the object
     */
    public DecoInstance(DecoTemplate template, RoomInstance parentRoom, Vector3 position, Quaternion rotation) {
        super(parentRoom.getPosition().cpy().add(position), rotation, 0, template.getScene(), btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);

        this.template = template;
        this.parentRoom = parentRoom;

        tryCreateShatterTrigger();
    }

    private void tryCreateShatterTrigger() {
        if (isRenderable && template.isDestructible()) {
            shatterTrigger = new WorldTrigger(collisionShape, transform, (Entity e) -> this.shatter(), ATTACK_GROUP, WorldTrigger.TriggerType.ONCE_ON_ENTER);
        }
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
        if (isRenderable) {
            collisionShape = Bullet.obtainStaticNodeShape(scene.modelInstance.nodes);
            motionState = new EntityMotionState(this, transform);

            Vector3 inertia = new Vector3();
            collisionShape.calculateLocalInertia(0, inertia);

            collider = new btRigidBody(0, motionState, collisionShape, inertia);
            collider.obtain();
            collider.setActivationState(Collision.DISABLE_DEACTIVATION);
            collider.setUserValue(id);
            dynamicsWorld.addRigidBody(collider);
        }
    }

    public void shatter() {
        if (shattered) return;
        Gdx.app.debug("DecoInstance", "Shattered deco " + id);
        shatterTrigger.destroy();
        shatterTrigger = null;
        shattered = true;
        dynamicsWorld.removeRigidBody(collider);
        motionState.release();
        collider.release();
        for (Node node : scene.modelInstance.nodes) {
            shatterNodes.add(new ShatterNode(node, this));
        }
    }

    public static class ShatterNode extends btMotionState {
        public Node node;
        btRigidBody collider;
        btCollisionShape shape;
        DecoInstance parent;
        BoundingBox bb;

        private static final int SHATTER_OBJECT_MASS = 5;

        public ShatterNode(Node node, DecoInstance deco) {
            this.parent = deco;
            this.node = node;

            bb = new BoundingBox();

            for (NodePart part : node.parts) {
                bb.ext(part.meshPart.mesh.calculateBoundingBox());
            }

            shape = new btBoxShape(bb.getDimensions(new Vector3()).cpy().scl(0.5f));
//            shape = Entity.obtainConvexHullShape(node.parts.get(0).meshPart.mesh, true);
//            shape = Bullet.obtainStaticNodeShape(node, true);

            Vector3 inertia = new Vector3();
            shape.calculateLocalInertia(SHATTER_OBJECT_MASS, inertia);

            collider = new btRigidBody(SHATTER_OBJECT_MASS, this, shape);
            collider.obtain();
            collider.setMotionState(this);

            // the translation to the original deco is applied twice, this solves that
            collider.setWorldTransform(new Matrix4().setToTranslation(bb.getCenter(new Vector3()).mul(deco.transform)));
            node.globalTransform.set(new Matrix4().set(parent.transform.getRotation(new Quaternion())));
            collider.setFriction(0.8f);
            dynamicsWorld.addRigidBody(collider);
        }

        private void update() {
            Gdx.app.debug("DecoInstance", "collider: " + collider.getWorldTransform().getTranslation(new Vector3()) + " global: " + node.globalTransform.getTranslation(new Vector3()) + " local: " + node.localTransform.getTranslation(new Vector3()));
        }

        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            // the translation to the original deco is applied twice, this solves that
            Vector3 translation = worldTrans.getTranslation(new Vector3()).sub(parent.transform.getTranslation(new Vector3()).add(bb.getCenter(new Vector3())));
            Quaternion rotation = worldTrans.getRotation(new Quaternion());
            node.globalTransform.set(new Matrix4().set(translation, rotation));
        }

        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set(node.globalTransform);
        }
    }

    @Override
    public void update(float delta) {
        if (!shattered) {
            motionState.setWorldTransform(transform);
        } else {
            shatterNodes.stream().sorted((n1, n2) -> (int) (n1.node.globalTransform.getTranslation(new Vector3()).sub(Main.player.getPosition()).len() - n2.node.globalTransform.getTranslation(new Vector3()).sub(Main.player.getPosition()).len())).collect(Collectors.toCollection(ArrayList::new)).getFirst().update();
//            shatterNodes.forEach(node -> node.update(delta));
        }
    }

    @Override
    public String getModelAddress() {
        return template.getModelPath();
    }

    @Override
    public synchronized void destroy() {
        if (shattered) {
            for (ShatterNode shatterNode : shatterNodes) {
                shatterNode.collider.release();
                shatterNode.collider = null;
                shatterNode.shape.release();
                shatterNode.shape = null;
                shatterNode.release();
            }
            shatterNodes.clear();
        } else {
            super.destroy();
            shatterTrigger.destroy();
        }
    }
}
