/*
 * Copyright © 2017 Damien Vergnet
 * 
 * This file is part of Jenealogio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.darmo_creations.model.family;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.darmo_creations.controllers.Undoable;

/**
 * A family has members and each member can be in relationships. The update methods do not use
 * directly the provided arguments but rather copy their fields to keep the family consistent.
 * 
 * @author Damien Vergnet
 */
public class Family implements Cloneable, Undoable {
  /** The global ID */
  private long globalId;
  /** This family's name */
  private String name;
  /** Members */
  private Set<FamilyMember> members;
  /** Relationships */
  private Set<Relationship> relations;

  /**
   * Creates a family with no members and no relations.
   * 
   * @param name family's name
   */
  public Family(String name) {
    this(0, name, new HashSet<>(), new HashSet<>());
  }

  /**
   * Creates a family with the given members and relations.
   * 
   * @param globalId global ID's initial value
   * @param name family's name
   * @param members the members
   * @param relations the relations
   */
  public Family(long globalId, String name, Set<FamilyMember> members, Set<Relationship> relations) {
    this.globalId = globalId;
    setName(name);
    this.members = Objects.requireNonNull(members);
    this.relations = Objects.requireNonNull(relations);
  }

  /**
   * @return this family's name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the family name.
   * 
   * @param name the new name
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name);
  }

  /**
   * Returns all the members. The set returned is a copy of the internal one.
   * 
   * @return all the members
   */
  public Set<FamilyMember> getAllMembers() {
    Set<FamilyMember> members = new HashSet<>();
    this.members.forEach(m -> members.add(m.clone()));
    return members;
  }

  /**
   * Gets the member with the given ID.
   * 
   * @param id the ID
   * @return the member or nothing if none were found
   */
  public Optional<FamilyMember> getMember(long id) {
    return this.members.stream().filter(member -> member.getId() == id).findAny();
  }

  /**
   * Adds a member to the family. If the given member already exist in this family, nothing will
   * happen.
   * 
   * @param member the new member
   */
  public void addMember(FamilyMember member) {
    if (!this.members.contains(member)) {
      this.members.add(member.clone(getNextMemberId()));
    }
  }

  /**
   * Updates an existing member. If the member does not exist in this family, nothing will happen.
   * 
   * @param member the member's updated data
   */
  public void updateMember(FamilyMember member) {
    if (this.members.contains(member)) {
      this.members.remove(member);
      this.members.add(member.clone());
    }
  }

  /**
   * Deletes a member from the family. Also removes associated relation. If the member does not
   * exist in this family, nothing will happen.
   * 
   * @param id the ID of the member to remove
   */
  public void removeMember(long id) {
    for (Iterator<Relationship> it = this.relations.iterator(); it.hasNext();) {
      Relationship relation = it.next();
      if (relation.isInRelationship(id))
        it.remove();
      else if (relation.isChild(id))
        relation.removeChild(id);
    }
    this.members.removeIf(m -> m.getId() == id);
  }

  /**
   * Returns all the relations. The set returned is a copy of the internal one.
   * 
   * @return all the relations
   */
  public Set<Relationship> getAllRelations() {
    Set<Relationship> relations = new HashSet<>();
    this.relations.forEach(r -> relations.add(r.clone()));
    return relations;
  }

  /**
   * Gets the relation for the given members.
   * 
   * @param id1 the first ID
   * @param id2 the second ID
   * @return the relationship between the two IDs
   */
  public Optional<Relationship> getRelation(long id1, long id2) {
    return this.relations.stream().filter(r -> r.isInRelationship(id1) && r.isInRelationship(id2)).findAny();
  }

  /**
   * Gets the relations for the given member. This method checks for every relation if one of the
   * two partners is the given member.
   * 
   * @param memberId the member's ID
   * @return the relations
   */
  public Set<Relationship> getRelations(long memberId) {
    return this.relations.stream().filter(relation -> relation.isInRelationship(memberId)).collect(Collectors.toSet());
  }

  /**
   * Adds a relation. If one of the two spouses is already married or the relation already exists,
   * nothing will happen.
   * 
   * @param relation the new relation
   */
  public void addRelation(Relationship relation) {
    if (!this.relations.contains(relation) && !areInRelationship(relation.getPartner1(), relation.getPartner2())) {
      this.relations.add(relation.clone());
    }
  }

  /**
   * Updates the given relation. If the relation does not exist in this family, nothing will happen.
   * 
   * @param relation the relation's new data
   */
  public void updateRelation(Relationship relation) {
    if (this.relations.contains(relation)) {
      relation.getChildren().forEach(id -> {
        if (!getMember(id).isPresent())
          throw new IllegalStateException("member ID '" + id + "' does not exist");
      });
      this.relations.remove(relation);
      this.relations.add(relation.clone());
    }
  }

  /**
   * Deletes a relation. If the relation does not exist in this family, nothing will happen.
   * 
   * @param relation the relation to delete
   */
  public void removeRelationship(Relationship relation) {
    this.relations.remove(relation);
  }

  /**
   * Tells if two members are in a relationship.
   * 
   * @param id1 first member ID
   * @param id2 second member ID
   * @return true if and only if they are in a relationship
   */
  public boolean areInRelationship(long id1, long id2) {
    return this.relations.stream().anyMatch(relation -> relation.isInRelationship(id1) && relation.isInRelationship(id2));
  }

  /**
   * Tells if a member has any known parent.
   * 
   * @param memberId member's ID
   * @return true if and only if the member has known parents
   */
  public boolean hasParents(long memberId) {
    return this.relations.stream().anyMatch(w -> w.getChildren().contains(memberId));
  }

  /**
   * Returns all members that can be children of the given couple. If the argument is null, all
   * members are returned.
   * 
   * @param relation the couple
   * @return a list of all potential children
   */
  public Set<FamilyMember> getPotentialChildren(Relationship relation) {
    if (relation == null)
      return getAllMembers();
    return getPotentialChildren(getMember(relation.getPartner1()).get(), getMember(relation.getPartner2()).get(), relation.getChildren());
  }

  /**
   * Returns all members that can be children of the given couple. If one of the partners is null,
   * all members are returned.
   * 
   * @param partner1 the first partner
   * @param partner2 the second partner
   * @param children the children
   * @return a list of potential children
   */
  public Set<FamilyMember> getPotentialChildren(FamilyMember partner1, FamilyMember partner2, Set<Long> children) {
    Set<FamilyMember> all = getAllMembers();

    if (partner1 == null || partner2 == null)
      return all;

    all.remove(partner1);
    all.remove(partner2);

    if (partner1.getBirthDate().isPresent() || partner2.getBirthDate().isPresent()) {
      // Filter out all members that are older than the youngest spouse.
      FamilyMember youngest = null;

      if (partner1.getBirthDate().isPresent() && partner2.getBirthDate().isPresent()) {
        youngest = partner1.compareBirthdays(partner2).get() > 0 ? partner1 : partner2;
      }
      else if (partner1.getBirthDate().isPresent()) {
        youngest = partner1;
      }
      else {
        youngest = partner2;
      }
      final FamilyMember y = youngest;
      all.removeIf(m -> m.compareBirthdays(y).orElse(1) <= 0);
    }

    all.removeIf(m -> children.contains(m.getId()) || hasParents(m.getId()));

    return all;
  }

  /**
   * @return the global member ID
   */
  public long getGlobalId() {
    return this.globalId;
  }

  /**
   * @return the next member ID
   */
  private long getNextMemberId() {
    return this.globalId++;
  }

  @Override
  public String toString() {
    return getName() + this.members + "," + this.relations;
  }

  @Override
  public Family clone() {
    return new Family(this.globalId, this.name, getAllMembers(), getAllRelations());
  }
}