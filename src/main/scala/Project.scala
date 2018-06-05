import org.apache.spark.sql.simba.SimbaSession
import org.apache.spark.sql.simba.index.RTreeType

object Project {
  case class PointData(trajectoryIdentification: String, objectIdentification: String, longitude: Double, latitude: Double, time: String)

  def main(args: Array[String]): Unit = {

    val simbaSession = SimbaSession
      .builder()
      .master("local[*]")
      .appName("SparkSessionForSimba")
      .config("simba.index.partitions", "20")
      .getOrCreate()

    part1(simbaSession)
    simbaSession.stop()
    simbaSession.close()
  }

  private def part1(simba: SimbaSession): Unit = {
    var ds = simba.read.option("inferSchema", "true").csv("/home/kjkamgar/Downloads/trajectories.csv").cache()
    ds = ds.withColumnRenamed("_c0", "trajectoryIdentification")
    ds = ds.withColumnRenamed("_c1", "objectIdentification")
    ds = ds.withColumnRenamed("_c2", "longitude")
    ds = ds.withColumnRenamed("_c3", "latitude")
    ds = ds.withColumnRenamed("_c4", "timeRead")

    var ds2 = simba.read.option("inferSchema", "true").csv("/home/kjkamgar/Downloads/POIs.csv").cache()

    ds2 = ds2.withColumnRenamed("_c0", "objectIdentification")
    ds2 = ds2.withColumnRenamed("_c1", "description")
    ds2 = ds2.withColumnRenamed("_c2", "longitude")
    ds2 = ds2.withColumnRenamed("_c3", "latitude")

    println("---------------------------------------------------------")
    ds.printSchema()
    ds2.printSchema()
    ds2.show(20)

    ds.createOrReplaceTempView("trajectory")

    ds2.createOrReplaceTempView("poi")
    


//    simba.indexTable("trajectory", RTreeType, "testtree",  Array("longitude", "latitude") )
//    simba.indexTable("poi", RTreeType, "poisIndex",  Array("longitude", "latitude") )
//    simba.loadIndex("poisIndex", "~/Downloads/POIsIndex")
//    simba.persistIndex("poisIndex", "~/Downloads/POIsIndex")

    import simba.simbaImplicits._
//    ds2.range(Array("longitude", "latitude"),Array(-339220.0,  4444725),Array(-309375.0, 4478070.0)).where("description LIKE \"amenity=restaurant\"").show()
//    ds2.sqlContext.sql("SELECT * FROM trajectory WHERE POINT(longitude, latitude) IN RANGE(POINT(-339220.0,  4444725), POINT(-309375.0, 4478070.0))").show()
    //http://epsg.io/4799 shows that the unit of this system is a meter. so is should be good like this
    var tmp = ds.filter("date_format(timeRead, 'u') <= 5").circleRange(Array("longitude", "latitude"), Array(-322357.0, 4463408.0), 2000)
    

  //  tmp.sortBy(    
    

//    tmp.show()
//    tmp.select("objectIdentification", "timeRead").groupBy("objectIdentification")
    println("------------------------------------------------------------------")
  }

  def question(ds : SimbaSession) : Unit ={
    // -324297.5  4461397.5
    // upper left  (-339220, 4478070)      (-324297.5, 4461397.5)
    // upper right (-324297.5, 4478070)    (-309375, 4461397.5)
    // lower left  (-339220, 4461397.5)    (-324297.5, 4444725)  
    // lower right (-324297.5,4478070)     (-309375, 4444725)
    
      
   
  }

  def question4(ds : SimbaSession) : Unit ={
     var df = ds.sql("Select * from trajectories where date_format(timeRead,'m') <=  6 and date_format(timeread,'m') >= 2")

     //ds2 = ds.select("*").from(ds).where("da <= 4 AND date >= 10")
     //ds.select(Array("x","y")).from(ds).circlerangle(
  }
}



