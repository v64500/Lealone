/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package com.lealone.db.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import com.lealone.common.util.MathUtils;
import com.lealone.common.util.StringUtils;
import com.lealone.common.util.Utils;
import com.lealone.db.SysProperties;

/**
 * Implementation of the BINARY data type.
 * It is also the base class for ValueJavaObject.
 */
public class ValueBytes extends Value {

    private static final ValueBytes EMPTY = new ValueBytes(Utils.EMPTY_BYTES);

    /**
     * The value.
     */
    protected byte[] value;

    /**
     * The hash code.
     */
    protected int hash;

    protected ValueBytes(byte[] v) {
        this.value = v;
    }

    /**
     * Get or create a bytes value for the given byte array.
     * Clone the data.
     *
     * @param b the byte array
     * @return the value
     */
    public static ValueBytes get(byte[] b) {
        if (b.length == 0) {
            return EMPTY;
        }
        b = Utils.cloneByteArray(b);
        return getNoCopy(b);
    }

    /**
     * Get or create a bytes value for the given byte array.
     * Do not clone the date.
     *
     * @param b the byte array
     * @return the value
     */
    public static ValueBytes getNoCopy(byte[] b) {
        if (b.length == 0) {
            return EMPTY;
        }
        ValueBytes obj = new ValueBytes(b);
        if (b.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        return (ValueBytes) Value.cache(obj);
    }

    @Override
    public int getType() {
        return Value.BYTES;
    }

    @Override
    public String getSQL() {
        return "X'" + StringUtils.convertBytesToHex(getBytesNoCopy()) + "'";
    }

    @Override
    public byte[] getBytesNoCopy() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        return Utils.cloneByteArray(getBytesNoCopy());
    }

    @Override
    protected int compareSecure(Value v, CompareMode mode) {
        byte[] v2 = ((ValueBytes) v).value;
        if (mode.isBinaryUnsigned()) {
            return Utils.compareNotNullUnsigned(value, v2);
        }
        return Utils.compareNotNullSigned(value, v2);
    }

    @Override
    public String getString() {
        return StringUtils.convertBytesToHex(value);
    }

    @Override
    public long getPrecision() {
        return value.length;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Utils.getByteArrayHash(value);
        }
        return hash;
    }

    @Override
    public Object getObject() {
        return getBytes();
    }

    @Override
    public void set(PreparedStatement prep, int parameterIndex) throws SQLException {
        prep.setBytes(parameterIndex, value);
    }

    @Override
    public int getDisplaySize() {
        return MathUtils.convertLongToInt(value.length * 2L);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueBytes && Arrays.equals(value, ((ValueBytes) other).value);
    }

    @Override
    public Value convertPrecision(long precision, boolean force) {
        if (value.length <= precision) {
            return this;
        }
        int len = MathUtils.convertLongToInt(precision);
        byte[] buff = new byte[len];
        System.arraycopy(value, 0, buff, 0, len);
        return get(buff);
    }

    @Override
    public int getMemory() {
        return value.length + 20;
    }
}
