// automatically generated by the FlatBuffers compiler, do not modify

package vo.av.localfeatures.geometry.generated;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class IntTuple3 extends Struct {
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public IntTuple3 __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int v0() { return bb.getInt(bb_pos + 0); }
  public int v1() { return bb.getInt(bb_pos + 4); }
  public int v2() { return bb.getInt(bb_pos + 8); }

  public static int createIntTuple3(FlatBufferBuilder builder, int v0, int v1, int v2) {
    builder.prep(4, 12);
    builder.putInt(v2);
    builder.putInt(v1);
    builder.putInt(v0);
    return builder.offset();
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public IntTuple3 get(int j) { return get(new IntTuple3(), j); }
    public IntTuple3 get(IntTuple3 obj, int j) {  return obj.__assign(__element(j), bb); }
  }
}

