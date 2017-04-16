package net.darmo_creations.model.family;

import java.util.Objects;

/**
 * This class represents a dummy family member.
 * 
 * @author Damien Vergnet
 */
public class DummyFamilyMember extends FamilyMember {
  public DummyFamilyMember(String name) {
    super(null, Objects.requireNonNull(name), null, Gender.UNKNOW, null, null, null, null);
  }

  @Override
  void setName(String name) {
    super.setName(Objects.requireNonNull(name));
  }

  @Override
  public String toString() {
    return getName().get();
  }
}
