/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package eu.proasense.internal;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-6-4")
public class RecommendationEvent implements org.apache.thrift.TBase<RecommendationEvent, RecommendationEvent._Fields>, java.io.Serializable, Cloneable, Comparable<RecommendationEvent> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RecommendationEvent");

  private static final org.apache.thrift.protocol.TField RECOMMENDATION_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("recommendationId", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField ACTION_FIELD_DESC = new org.apache.thrift.protocol.TField("action", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("timestamp", org.apache.thrift.protocol.TType.I64, (short)3);
  private static final org.apache.thrift.protocol.TField ACTOR_FIELD_DESC = new org.apache.thrift.protocol.TField("actor", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField EVENT_PROPERTIES_FIELD_DESC = new org.apache.thrift.protocol.TField("eventProperties", org.apache.thrift.protocol.TType.MAP, (short)5);
  private static final org.apache.thrift.protocol.TField EVENT_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("eventName", org.apache.thrift.protocol.TType.STRING, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new RecommendationEventStandardSchemeFactory());
    schemes.put(TupleScheme.class, new RecommendationEventTupleSchemeFactory());
  }

  public String recommendationId; // required
  public String action; // required
  public long timestamp; // required
  public String actor; // required
  public Map<String,ComplexValue> eventProperties; // required
  public String eventName; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RECOMMENDATION_ID((short)1, "recommendationId"),
    ACTION((short)2, "action"),
    TIMESTAMP((short)3, "timestamp"),
    ACTOR((short)4, "actor"),
    EVENT_PROPERTIES((short)5, "eventProperties"),
    EVENT_NAME((short)6, "eventName");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // RECOMMENDATION_ID
          return RECOMMENDATION_ID;
        case 2: // ACTION
          return ACTION;
        case 3: // TIMESTAMP
          return TIMESTAMP;
        case 4: // ACTOR
          return ACTOR;
        case 5: // EVENT_PROPERTIES
          return EVENT_PROPERTIES;
        case 6: // EVENT_NAME
          return EVENT_NAME;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TIMESTAMP_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RECOMMENDATION_ID, new org.apache.thrift.meta_data.FieldMetaData("recommendationId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ACTION, new org.apache.thrift.meta_data.FieldMetaData("action", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("timestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "long")));
    tmpMap.put(_Fields.ACTOR, new org.apache.thrift.meta_data.FieldMetaData("actor", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.EVENT_PROPERTIES, new org.apache.thrift.meta_data.FieldMetaData("eventProperties", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ComplexValue.class))));
    tmpMap.put(_Fields.EVENT_NAME, new org.apache.thrift.meta_data.FieldMetaData("eventName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RecommendationEvent.class, metaDataMap);
  }

  public RecommendationEvent() {
  }

  public RecommendationEvent(
    String recommendationId,
    String action,
    long timestamp,
    String actor,
    Map<String,ComplexValue> eventProperties,
    String eventName)
  {
    this();
    this.recommendationId = recommendationId;
    this.action = action;
    this.timestamp = timestamp;
    setTimestampIsSet(true);
    this.actor = actor;
    this.eventProperties = eventProperties;
    this.eventName = eventName;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RecommendationEvent(RecommendationEvent other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetRecommendationId()) {
      this.recommendationId = other.recommendationId;
    }
    if (other.isSetAction()) {
      this.action = other.action;
    }
    this.timestamp = other.timestamp;
    if (other.isSetActor()) {
      this.actor = other.actor;
    }
    if (other.isSetEventProperties()) {
      Map<String,ComplexValue> __this__eventProperties = new HashMap<String,ComplexValue>(other.eventProperties.size());
      for (Map.Entry<String, ComplexValue> other_element : other.eventProperties.entrySet()) {

        String other_element_key = other_element.getKey();
        ComplexValue other_element_value = other_element.getValue();

        String __this__eventProperties_copy_key = other_element_key;

        ComplexValue __this__eventProperties_copy_value = new ComplexValue(other_element_value);

        __this__eventProperties.put(__this__eventProperties_copy_key, __this__eventProperties_copy_value);
      }
      this.eventProperties = __this__eventProperties;
    }
    if (other.isSetEventName()) {
      this.eventName = other.eventName;
    }
  }

  public RecommendationEvent deepCopy() {
    return new RecommendationEvent(this);
  }

  @Override
  public void clear() {
    this.recommendationId = null;
    this.action = null;
    setTimestampIsSet(false);
    this.timestamp = 0;
    this.actor = null;
    this.eventProperties = null;
    this.eventName = null;
  }

  public String getRecommendationId() {
    return this.recommendationId;
  }

  public RecommendationEvent setRecommendationId(String recommendationId) {
    this.recommendationId = recommendationId;
    return this;
  }

  public void unsetRecommendationId() {
    this.recommendationId = null;
  }

  /** Returns true if field recommendationId is set (has been assigned a value) and false otherwise */
  public boolean isSetRecommendationId() {
    return this.recommendationId != null;
  }

  public void setRecommendationIdIsSet(boolean value) {
    if (!value) {
      this.recommendationId = null;
    }
  }

  public String getAction() {
    return this.action;
  }

  public RecommendationEvent setAction(String action) {
    this.action = action;
    return this;
  }

  public void unsetAction() {
    this.action = null;
  }

  /** Returns true if field action is set (has been assigned a value) and false otherwise */
  public boolean isSetAction() {
    return this.action != null;
  }

  public void setActionIsSet(boolean value) {
    if (!value) {
      this.action = null;
    }
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public RecommendationEvent setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    setTimestampIsSet(true);
    return this;
  }

  public void unsetTimestamp() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  /** Returns true if field timestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetTimestamp() {
    return EncodingUtils.testBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  public void setTimestampIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TIMESTAMP_ISSET_ID, value);
  }

  public String getActor() {
    return this.actor;
  }

  public RecommendationEvent setActor(String actor) {
    this.actor = actor;
    return this;
  }

  public void unsetActor() {
    this.actor = null;
  }

  /** Returns true if field actor is set (has been assigned a value) and false otherwise */
  public boolean isSetActor() {
    return this.actor != null;
  }

  public void setActorIsSet(boolean value) {
    if (!value) {
      this.actor = null;
    }
  }

  public int getEventPropertiesSize() {
    return (this.eventProperties == null) ? 0 : this.eventProperties.size();
  }

  public void putToEventProperties(String key, ComplexValue val) {
    if (this.eventProperties == null) {
      this.eventProperties = new HashMap<String,ComplexValue>();
    }
    this.eventProperties.put(key, val);
  }

  public Map<String,ComplexValue> getEventProperties() {
    return this.eventProperties;
  }

  public RecommendationEvent setEventProperties(Map<String,ComplexValue> eventProperties) {
    this.eventProperties = eventProperties;
    return this;
  }

  public void unsetEventProperties() {
    this.eventProperties = null;
  }

  /** Returns true if field eventProperties is set (has been assigned a value) and false otherwise */
  public boolean isSetEventProperties() {
    return this.eventProperties != null;
  }

  public void setEventPropertiesIsSet(boolean value) {
    if (!value) {
      this.eventProperties = null;
    }
  }

  public String getEventName() {
    return this.eventName;
  }

  public RecommendationEvent setEventName(String eventName) {
    this.eventName = eventName;
    return this;
  }

  public void unsetEventName() {
    this.eventName = null;
  }

  /** Returns true if field eventName is set (has been assigned a value) and false otherwise */
  public boolean isSetEventName() {
    return this.eventName != null;
  }

  public void setEventNameIsSet(boolean value) {
    if (!value) {
      this.eventName = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case RECOMMENDATION_ID:
      if (value == null) {
        unsetRecommendationId();
      } else {
        setRecommendationId((String)value);
      }
      break;

    case ACTION:
      if (value == null) {
        unsetAction();
      } else {
        setAction((String)value);
      }
      break;

    case TIMESTAMP:
      if (value == null) {
        unsetTimestamp();
      } else {
        setTimestamp((Long)value);
      }
      break;

    case ACTOR:
      if (value == null) {
        unsetActor();
      } else {
        setActor((String)value);
      }
      break;

    case EVENT_PROPERTIES:
      if (value == null) {
        unsetEventProperties();
      } else {
        setEventProperties((Map<String,ComplexValue>)value);
      }
      break;

    case EVENT_NAME:
      if (value == null) {
        unsetEventName();
      } else {
        setEventName((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case RECOMMENDATION_ID:
      return getRecommendationId();

    case ACTION:
      return getAction();

    case TIMESTAMP:
      return Long.valueOf(getTimestamp());

    case ACTOR:
      return getActor();

    case EVENT_PROPERTIES:
      return getEventProperties();

    case EVENT_NAME:
      return getEventName();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case RECOMMENDATION_ID:
      return isSetRecommendationId();
    case ACTION:
      return isSetAction();
    case TIMESTAMP:
      return isSetTimestamp();
    case ACTOR:
      return isSetActor();
    case EVENT_PROPERTIES:
      return isSetEventProperties();
    case EVENT_NAME:
      return isSetEventName();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RecommendationEvent)
      return this.equals((RecommendationEvent)that);
    return false;
  }

  public boolean equals(RecommendationEvent that) {
    if (that == null)
      return false;

    boolean this_present_recommendationId = true && this.isSetRecommendationId();
    boolean that_present_recommendationId = true && that.isSetRecommendationId();
    if (this_present_recommendationId || that_present_recommendationId) {
      if (!(this_present_recommendationId && that_present_recommendationId))
        return false;
      if (!this.recommendationId.equals(that.recommendationId))
        return false;
    }

    boolean this_present_action = true && this.isSetAction();
    boolean that_present_action = true && that.isSetAction();
    if (this_present_action || that_present_action) {
      if (!(this_present_action && that_present_action))
        return false;
      if (!this.action.equals(that.action))
        return false;
    }

    boolean this_present_timestamp = true;
    boolean that_present_timestamp = true;
    if (this_present_timestamp || that_present_timestamp) {
      if (!(this_present_timestamp && that_present_timestamp))
        return false;
      if (this.timestamp != that.timestamp)
        return false;
    }

    boolean this_present_actor = true && this.isSetActor();
    boolean that_present_actor = true && that.isSetActor();
    if (this_present_actor || that_present_actor) {
      if (!(this_present_actor && that_present_actor))
        return false;
      if (!this.actor.equals(that.actor))
        return false;
    }

    boolean this_present_eventProperties = true && this.isSetEventProperties();
    boolean that_present_eventProperties = true && that.isSetEventProperties();
    if (this_present_eventProperties || that_present_eventProperties) {
      if (!(this_present_eventProperties && that_present_eventProperties))
        return false;
      if (!this.eventProperties.equals(that.eventProperties))
        return false;
    }

    boolean this_present_eventName = true && this.isSetEventName();
    boolean that_present_eventName = true && that.isSetEventName();
    if (this_present_eventName || that_present_eventName) {
      if (!(this_present_eventName && that_present_eventName))
        return false;
      if (!this.eventName.equals(that.eventName))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_recommendationId = true && (isSetRecommendationId());
    list.add(present_recommendationId);
    if (present_recommendationId)
      list.add(recommendationId);

    boolean present_action = true && (isSetAction());
    list.add(present_action);
    if (present_action)
      list.add(action);

    boolean present_timestamp = true;
    list.add(present_timestamp);
    if (present_timestamp)
      list.add(timestamp);

    boolean present_actor = true && (isSetActor());
    list.add(present_actor);
    if (present_actor)
      list.add(actor);

    boolean present_eventProperties = true && (isSetEventProperties());
    list.add(present_eventProperties);
    if (present_eventProperties)
      list.add(eventProperties);

    boolean present_eventName = true && (isSetEventName());
    list.add(present_eventName);
    if (present_eventName)
      list.add(eventName);

    return list.hashCode();
  }

  @Override
  public int compareTo(RecommendationEvent other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetRecommendationId()).compareTo(other.isSetRecommendationId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRecommendationId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.recommendationId, other.recommendationId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAction()).compareTo(other.isSetAction());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAction()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.action, other.action);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTimestamp()).compareTo(other.isSetTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timestamp, other.timestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetActor()).compareTo(other.isSetActor());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetActor()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.actor, other.actor);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEventProperties()).compareTo(other.isSetEventProperties());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEventProperties()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.eventProperties, other.eventProperties);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEventName()).compareTo(other.isSetEventName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEventName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.eventName, other.eventName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("RecommendationEvent(");
    boolean first = true;

    sb.append("recommendationId:");
    if (this.recommendationId == null) {
      sb.append("null");
    } else {
      sb.append(this.recommendationId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("action:");
    if (this.action == null) {
      sb.append("null");
    } else {
      sb.append(this.action);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("timestamp:");
    sb.append(this.timestamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("actor:");
    if (this.actor == null) {
      sb.append("null");
    } else {
      sb.append(this.actor);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("eventProperties:");
    if (this.eventProperties == null) {
      sb.append("null");
    } else {
      sb.append(this.eventProperties);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("eventName:");
    if (this.eventName == null) {
      sb.append("null");
    } else {
      sb.append(this.eventName);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (recommendationId == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'recommendationId' was not present! Struct: " + toString());
    }
    if (action == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'action' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'timestamp' because it's a primitive and you chose the non-beans generator.
    if (actor == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'actor' was not present! Struct: " + toString());
    }
    if (eventProperties == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'eventProperties' was not present! Struct: " + toString());
    }
    if (eventName == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'eventName' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RecommendationEventStandardSchemeFactory implements SchemeFactory {
    public RecommendationEventStandardScheme getScheme() {
      return new RecommendationEventStandardScheme();
    }
  }

  private static class RecommendationEventStandardScheme extends StandardScheme<RecommendationEvent> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RecommendationEvent struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RECOMMENDATION_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.recommendationId = iprot.readString();
              struct.setRecommendationIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ACTION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.action = iprot.readString();
              struct.setActionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TIMESTAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.timestamp = iprot.readI64();
              struct.setTimestampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ACTOR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.actor = iprot.readString();
              struct.setActorIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // EVENT_PROPERTIES
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map54 = iprot.readMapBegin();
                struct.eventProperties = new HashMap<String,ComplexValue>(2*_map54.size);
                String _key55;
                ComplexValue _val56;
                for (int _i57 = 0; _i57 < _map54.size; ++_i57)
                {
                  _key55 = iprot.readString();
                  _val56 = new ComplexValue();
                  _val56.read(iprot);
                  struct.eventProperties.put(_key55, _val56);
                }
                iprot.readMapEnd();
              }
              struct.setEventPropertiesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // EVENT_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.eventName = iprot.readString();
              struct.setEventNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetTimestamp()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'timestamp' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, RecommendationEvent struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.recommendationId != null) {
        oprot.writeFieldBegin(RECOMMENDATION_ID_FIELD_DESC);
        oprot.writeString(struct.recommendationId);
        oprot.writeFieldEnd();
      }
      if (struct.action != null) {
        oprot.writeFieldBegin(ACTION_FIELD_DESC);
        oprot.writeString(struct.action);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TIMESTAMP_FIELD_DESC);
      oprot.writeI64(struct.timestamp);
      oprot.writeFieldEnd();
      if (struct.actor != null) {
        oprot.writeFieldBegin(ACTOR_FIELD_DESC);
        oprot.writeString(struct.actor);
        oprot.writeFieldEnd();
      }
      if (struct.eventProperties != null) {
        oprot.writeFieldBegin(EVENT_PROPERTIES_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, struct.eventProperties.size()));
          for (Map.Entry<String, ComplexValue> _iter58 : struct.eventProperties.entrySet())
          {
            oprot.writeString(_iter58.getKey());
            _iter58.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.eventName != null) {
        oprot.writeFieldBegin(EVENT_NAME_FIELD_DESC);
        oprot.writeString(struct.eventName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RecommendationEventTupleSchemeFactory implements SchemeFactory {
    public RecommendationEventTupleScheme getScheme() {
      return new RecommendationEventTupleScheme();
    }
  }

  private static class RecommendationEventTupleScheme extends TupleScheme<RecommendationEvent> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RecommendationEvent struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.recommendationId);
      oprot.writeString(struct.action);
      oprot.writeI64(struct.timestamp);
      oprot.writeString(struct.actor);
      {
        oprot.writeI32(struct.eventProperties.size());
        for (Map.Entry<String, ComplexValue> _iter59 : struct.eventProperties.entrySet())
        {
          oprot.writeString(_iter59.getKey());
          _iter59.getValue().write(oprot);
        }
      }
      oprot.writeString(struct.eventName);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RecommendationEvent struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.recommendationId = iprot.readString();
      struct.setRecommendationIdIsSet(true);
      struct.action = iprot.readString();
      struct.setActionIsSet(true);
      struct.timestamp = iprot.readI64();
      struct.setTimestampIsSet(true);
      struct.actor = iprot.readString();
      struct.setActorIsSet(true);
      {
        org.apache.thrift.protocol.TMap _map60 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.eventProperties = new HashMap<String,ComplexValue>(2*_map60.size);
        String _key61;
        ComplexValue _val62;
        for (int _i63 = 0; _i63 < _map60.size; ++_i63)
        {
          _key61 = iprot.readString();
          _val62 = new ComplexValue();
          _val62.read(iprot);
          struct.eventProperties.put(_key61, _val62);
        }
      }
      struct.setEventPropertiesIsSet(true);
      struct.eventName = iprot.readString();
      struct.setEventNameIsSet(true);
    }
  }

}

