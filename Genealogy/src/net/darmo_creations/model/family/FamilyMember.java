package net.darmo_creations.model.family;

import java.awt.image.BufferedImage;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

import net.darmo_creations.model.Date;
import net.darmo_creations.util.CalendarUtil;
import net.darmo_creations.util.Images;
import net.darmo_creations.util.Nullable;

/**
 * A family member has an internal ID, a name, first name, gender, birth and death date and
 * location, an image. It can be copied through the <code>copy</code> method.
 * <p>
 * Two members can be compared. The comparison is done on the internal IDs. The <code>equals</code>
 * method is implemented as follow:
 * 
 * <pre>
 * public boolean equals(Object o) {
 *   if (o instanceof FamilyMember)
 *     return getId() == ((FamilyMember) o).getId();
 *   return false;
 * }
 * </pre>
 * </p>
 * 
 * @author Damien Vergnet
 */
public class FamilyMember implements Comparable<FamilyMember> {
  private final long id;
  private BufferedImage image;
  private String name;
  private String firstName;
  private Gender gender;
  private Date birthDate;
  private String birthLocation;
  private Date deathDate;
  private String deathLocation;

  /**
   * Creates a new member with no ID.
   * 
   * @param image the profile image
   * @param name the name
   * @param firstName the first name
   * @param gender the gender (can be <code>Gender.UNKNOWN</code>)
   * @param birthDate the birthday
   * @param birthLocation the birth location
   * @param deathDate the death date
   * @param deathLocation the death location
   */
  public FamilyMember(@Nullable BufferedImage image, @Nullable String name, @Nullable String firstName, Gender gender,
      @Nullable Date birthDate, @Nullable String birthLocation, @Nullable Date deathDate, @Nullable String deathLocation) {
    this(-1, image, name, firstName, gender, birthDate, birthLocation, deathDate, deathLocation);
  }

  /**
   * Creates a new member with the given ID.
   * 
   * @param id internal ID
   * @param image the profile image
   * @param name the name
   * @param firstName the first name
   * @param gender the gender (can be <code>Gender.UNKNOWN</code>)
   * @param birthDate the birthday
   * @param birthLocation the birth location
   * @param deathDate the death date
   * @param deathLocation the death location
   */
  public FamilyMember(long id, @Nullable BufferedImage image, @Nullable String name, @Nullable String firstName, Gender gender,
      @Nullable Date birthDate, @Nullable String birthLocation, @Nullable Date deathDate, @Nullable String deathLocation) {
    this.id = id;
    this.image = image != null ? Images.deepCopy(image) : null;
    this.name = name;
    this.firstName = firstName;
    this.gender = Objects.requireNonNull(gender);
    this.birthDate = birthDate != null ? birthDate.clone() : null;
    this.birthLocation = birthLocation;
    this.deathDate = deathDate != null ? deathDate.clone() : null;
    this.deathLocation = deathLocation;
  }

  /**
   * @return the internal ID
   */
  public final long getId() {
    return this.id;
  }

  /**
   * @return the profile image
   */
  public Optional<BufferedImage> getImage() {
    return Optional.ofNullable(this.image != null ? Images.deepCopy(this.image) : null);
  }

  /**
   * Sets the profile image. May be null.
   * 
   * @param image the new profile image
   */
  void setImage(@Nullable BufferedImage image) {
    this.image = image != null ? Images.deepCopy(image) : null;
  }

  /**
   * @return the name
   */
  public Optional<String> getName() {
    return Optional.ofNullable(this.name);
  }

  /**
   * Sets the name. May be null.
   * 
   * @param name the new name
   */
  void setName(@Nullable String name) {
    this.name = name;
  }

  /**
   * @return the first name
   */
  public Optional<String> getFirstName() {
    return Optional.ofNullable(this.firstName);
  }

  /**
   * Sets the first name. May be null.
   * 
   * @param firstName the new first name
   */
  void setFirstName(@Nullable String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return true if and only if this person is a man
   */
  public boolean isMan() {
    return this.gender.isMan();
  }

  /**
   * @return true if and only if this person is a woman
   */
  public boolean isWoman() {
    return this.gender.isWoman();
  }

  /**
   * @return the gender
   */
  public Gender getGender() {
    return this.gender;
  }

  /**
   * Sets the gender. Cannot be null.
   * 
   * @param gender the new gender
   */
  void setGender(Gender gender) {
    this.gender = Objects.requireNonNull(gender);
  }

  /**
   * @return the birth date
   */
  public Optional<Date> getBirthDate() {
    return Optional.ofNullable(this.birthDate != null ? this.birthDate.clone() : null);
  }

  /**
   * Sets the birth date. May be null.
   * 
   * @param birthDate the new birth date
   */
  void setBirthDate(@Nullable Date birthDate) {
    this.birthDate = birthDate != null ? birthDate.clone() : null;
  }

  /**
   * Returns the member's age.<br/>
   * <i>Special cases</i>: if the birthday is not available, the age will be empty; if the death
   * date is not available, age will be calculated based on the current date.
   * 
   * @return the member's current age
   */
  public Optional<Period> getAge() {
    if (!getBirthDate().isPresent())
      return Optional.empty();

    Optional<Date> deathDate = getDeathDate();
    Date currentDate = deathDate.isPresent() ? deathDate.get() : CalendarUtil.getCurrentDate();
    Date birthDate = getBirthDate().get();
    int currentMonth = currentDate.getMonth();
    int birthMonth = birthDate.getMonth();
    int currentDay = currentDate.getDate();
    int birthDay = birthDate.getDate();
    int years = currentDate.getYear() - birthDate.getYear();

    // If the date is not yet behind, remove.
    if (birthMonth > currentMonth || birthMonth == currentMonth && birthDay > currentDay)
      years = years == 0 ? 0 : years - 1;

    return Optional.of(Period.ofYears(years));
  }

  /**
   * @return the birth location
   */
  public Optional<String> getBirthLocation() {
    return Optional.ofNullable(this.birthLocation);
  }

  /**
   * Sets the birth location. May be null.
   * 
   * @param birthLocation the new birth location
   */
  void setBirthPlace(@Nullable String birthLocation) {
    this.birthLocation = birthLocation;
  }

  /**
   * @return the death date
   */
  public Optional<Date> getDeathDate() {
    return Optional.ofNullable(this.deathDate != null ? this.deathDate.clone() : null);
  }

  /**
   * Sets the death date. May be null.
   * 
   * @param deathDate the new death date
   */
  void setDeathDate(@Nullable Date deathDate) {
    this.deathDate = deathDate != null ? deathDate.clone() : null;
  }

  /**
   * @return the death location
   */
  public Optional<String> getDeathLocation() {
    return Optional.ofNullable(this.deathLocation);
  }

  /**
   * Sets the death location. May be null.
   * 
   * @param deathLocation the new death location
   */
  void setDeathLocation(@Nullable String deathLocation) {
    this.deathLocation = deathLocation;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(this.id);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof FamilyMember)
      return getId() == ((FamilyMember) o).getId();
    return false;
  }

  @Override
  public String toString() {
    if (!getName().isPresent() && !getFirstName().isPresent())
      return "?";
    return getFirstName().orElse("?") + " " + getName().orElse("?");
  }

  /**
   * Copies this member and sets the copy's ID.
   * 
   * @param id the copy's ID
   * @return the copied person
   */
  FamilyMember copy(long id) {
    return new FamilyMember(id, getImage().orElse(null), getName().orElse(null), getFirstName().orElse(null), getGender(),
        getBirthDate().orElse(null), getBirthLocation().orElse(null), getDeathDate().orElse(null), getDeathLocation().orElse(null));
  }

  @Override
  public int compareTo(FamilyMember f) {
    return Long.compare(getId(), f.getId());
  }
}
