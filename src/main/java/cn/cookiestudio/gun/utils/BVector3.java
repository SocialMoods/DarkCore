package cn.cookiestudio.gun.utils;

import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;

public final class BVector3 {
   private Vector3 vector3;
   private double yaw;
   private double pitch;
   private double length;

   public static BVector3 fromLocation(Location location) {
      return fromLocation(location, 1.0D);
   }

   public static BVector3 fromLocation(Location location, double length) {
      return new BVector3(location.getYaw(), location.getPitch(), length);
   }

   public static BVector3 fromAngle(double yaw, double pitch) {
      return new BVector3(yaw, pitch, 1.0D);
   }

   public static BVector3 fromPos(Vector3 pos) {
      return new BVector3(pos);
   }

   public static BVector3 fromPos(double x, double y, double z) {
      return fromPos(new Vector3(x, y, z));
   }

   private BVector3(double yaw, double pitch, double length) {
      this.vector3 = getDirectionVector(yaw, pitch);
      this.yaw = getYawFromVector(this.vector3);
      this.pitch = getPitchFromVector(this.vector3);
      this.length = length;
   }

   private BVector3(Vector3 vector3) {
      this.yaw = getYawFromVector(vector3);
      this.pitch = getPitchFromVector(vector3);
      this.vector3 = getDirectionVector(this.yaw, this.pitch);
      this.length = vector3.length();
   }

   public BVector3 setYaw(double yaw) {
      this.vector3 = getDirectionVector(yaw, this.pitch);
      this.yaw = getYawFromVector(this.vector3);
      return this;
   }

   public BVector3 setPitch(double pitch) {
      this.vector3 = getDirectionVector(this.yaw, pitch);
      this.pitch = getPitchFromVector(this.vector3);
      return this;
   }

   public BVector3 rotateYaw(double yaw) {
      this.yaw += yaw;
      this.vector3 = getDirectionVector(this.yaw, this.pitch);
      this.yaw = getYawFromVector(this.vector3);
      return this;
   }

   public BVector3 rotatePitch(double pitch) {
      this.pitch += pitch;
      this.vector3 = getDirectionVector(this.yaw, this.pitch);
      this.pitch = getPitchFromVector(this.vector3);
      return this;
   }

   public BVector3 rotate(double yaw, double pitch) {
      this.pitch += pitch;
      this.yaw += yaw;
      this.vector3 = getDirectionVector(this.yaw, this.pitch);
      this.pitch = getPitchFromVector(this.vector3);
      this.pitch = getYawFromVector(this.vector3);
      return this;
   }

   public BVector3 add(double x, double y, double z) {
      Vector3 pos = this.vector3.multiply(this.length);
      pos.add(x, y, z);
      this.yaw = getYawFromVector(pos);
      this.pitch = getPitchFromVector(pos);
      this.vector3 = pos.normalize();
      this.length = pos.length();
      return this;
   }

   public BVector3 add(Vector3 vector3) {
      return this.add(vector3.x, vector3.y, vector3.z);
   }

   public Vector3 addToPos() {
      return new Vector3(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
   }

   public Vector3 addToPos(Vector3 pos) {
      return pos.add(this.vector3.x * this.length, this.vector3.y * this.length, this.vector3.z * this.length);
   }

   public BVector3 setLength(double length) {
      this.length = length;
      return this;
   }

   public BVector3 extend(double length) {
      if (this.length + length <= 0.0D) {
         throw new IllegalArgumentException("Vector length must bigger than zero");
      } else {
         this.length += length;
         return this;
      }
   }

   public double getYaw() {
      return this.yaw;
   }

   public double getPitch() {
      return this.pitch;
   }

   public Vector3 getDirectionVector() {
      return this.vector3.clone();
   }

   public Vector3 getUnclonedDirectionVector() {
      return this.vector3;
   }

   public static Vector3 getDirectionVector(double yaw, double pitch) {
      double pitch0 = StrictMath.toRadians(pitch + 90.0D);
      double yaw0 = StrictMath.toRadians(yaw + 90.0D);
      double x = StrictMath.sin(pitch0) * StrictMath.cos(yaw0);
      double z = StrictMath.sin(pitch0) * StrictMath.sin(yaw0);
      double y = StrictMath.cos(pitch0);
      return (new Vector3(x, y, z)).normalize();
   }

   public static double getYawFromVector(Vector3 vector) {
      double length = vector.x * vector.x + vector.z * vector.z;
      if (length == 0.0D) {
         return 0.0D;
      } else {
         double yaw = StrictMath.toDegrees(StrictMath.asin(-vector.x / StrictMath.sqrt(length)));
         return -vector.z > 0.0D ? 180.0D - yaw : (StrictMath.abs(yaw) < 1.0E-10D ? 0.0D : yaw);
      }
   }

   public static double getPitchFromVector(Vector3 vector) {
      double length = vector.x * vector.x + vector.z * vector.z + vector.y * vector.y;
      if (length == 0.0D) {
         return 0.0D;
      } else {
         double pitch = StrictMath.toDegrees(StrictMath.asin(-vector.y / StrictMath.sqrt(length)));
         return StrictMath.abs(pitch) < 1.0E-10D ? 0.0D : pitch;
      }
   }
}
