package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * All possible tags for decos
 * @author SkySourced
 */
public enum DecoTag {
    ALL((DecoTag) null), // all tags should have a parent trail to this
    // placement
    GROUND(ALL),
    CEILING(ALL),
    WALL(ALL),
    // properties
    LIGHT(ALL);

    final ArrayList<DecoTag> parents;
    final ArrayList<DecoTag> children;

    DecoTag(DecoTag... parent) {
        this.parents = new ArrayList<>();
        if(parent[0] != null) parents.addAll(List.of(parent));

        this.children = new ArrayList<>();
        for (DecoTag parentTag : parents) {
            parentTag.children.add(this);
        }
    }

    /**
     * Check if this tag is a child of the given parent
     * @param target the parent tag
     * @return true if this tag is a child of the parent, or if the tags are the same
     */
    public boolean isChildOf(DecoTag target) {
        if (this == target || target == ALL || this.parents.contains(target)) return true;

        HashSet<DecoTag> visited = new HashSet<>(); // targets compared already
        HashSet<DecoTag> seen = new HashSet<>(this.parents); // targets to compare
        while (true) {
            if (seen.isEmpty()) return false;
            if (seen.contains(target)) return true;
            for (DecoTag tag : seen) {
                visited.add(tag);
                for (DecoTag parent : tag.parents) {
                    if (parent == target) return true;
                    if (!visited.contains(parent)) seen.add(parent);
                }
                seen.remove(tag);
            }
        }
    }

    /**
     * Quick inversion of <code>isChildOf</code>
     * @param child the child tag
     * @return true if this tag is a parent of the child, or if the tags are the same
     */
    public boolean isParentOf(DecoTag child) {
        return child.isChildOf(this);
    }

    /**
     * Get the tag from the name
     * @param name the name of the tag
     * @return the tag
     */
    public static DecoTag fromString(String name) {
        try {
            return DecoTag.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            Gdx.app.error("DecoTag", "Invalid tag name: " + name);
            return null;
        }
    }

    /**
     * Get the name of the tag in lowercase
     * @return the name of the tag
     */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
