package com.ducksteam.needleseye.entity.enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * All possible tags for enemies
 * @author SkySourced
 */
public enum EnemyTag {
    ALL("all", (EnemyTag) null); // all tags should have a parent trail to this

    final String alias;
    final ArrayList<EnemyTag> parents;
    final ArrayList<EnemyTag> children;

    EnemyTag(String alias, EnemyTag... parent) {
        this.alias = alias;
        this.parents = new ArrayList<>();
        parents.addAll(List.of(parent));

        this.children = new ArrayList<>();
        for (EnemyTag parentTag : parents) {
            parentTag.children.add(this);
        }
    }

    /**
     * Check if this tag is a child of the given parent
     * @param target the parent tag
     * @return true if this tag is a child of the parent, or if the tags are the same
     */
    public boolean isChildOf(EnemyTag target) {
        if (this == target || target == ALL || this.parents.contains(target)) return true;

        HashSet<EnemyTag> visited = new HashSet<>(); // targets compared already
        HashSet<EnemyTag> seen = new HashSet<>(this.parents); // targets to compare
        while (true) {
            if (seen.isEmpty()) return false;
            if (seen.contains(target)) return true;
            for (EnemyTag tag : seen) {
                visited.add(tag);
                for (EnemyTag parent : tag.parents) {
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
    public boolean isParentOf(EnemyTag child) {
        return child.isChildOf(this);
    }
}
