/* Copyright (c) 2007 EntIT Software LLC, a Micro Focus company  -*- Java -*-*/
/* 
 * Description: Example User Defined Transform Function: Output top-k rows in each partition
 *
 * Create Date: June 1, 2013
 */

package com.vertica.JavaLibs;

import com.vertica.sdk.*;

import java.sql.*;

// TopK per partition
public class JdbcDemoFactory extends TransformFunctionFactory
{
    public static String JDBC_URL = "jdbc:vertica://192.168.1.206:5433/d2";
    public static String JDBC_USER = "dbadmin";
    public static String JDBC_PASS = "Vertica1!";

    @Override
    public TransformFunction createTransformFunction(ServerInterface srvInterface)
    { return new JdbcDemo(); }

    @Override
    public void getReturnType(ServerInterface srvInterface, SizedColumnTypes input_types, SizedColumnTypes output_types)
    {
        output_types.addInt("col1");
        output_types.addInt("col2");
        output_types.addVarchar(64, "col3");
    }

    @Override
    public void getPrototype(ServerInterface srvInterface, ColumnTypes argTypes, ColumnTypes returnType)
    {
        argTypes.addInt();
        argTypes.addInt();
        argTypes.addInt();

        returnType.addInt();
        returnType.addInt();
        returnType.addVarchar();
    }

    @Override
    public void getParameterType(ServerInterface srvInterface,
                                 SizedColumnTypes parameterTypes)
    {
        parameterTypes.addVarchar(64, "srcTable");
        parameterTypes.addVarchar(64, "dstTable");
    }

    public class JdbcDemo extends TransformFunction
    {
        @Override
        public void processPartition(ServerInterface srvInterface, PartitionReader input_reader, PartitionWriter output_writer)
                    throws UdfException, DestroyInvocation
        {
            ParamReader paramReader = srvInterface.getParamReader();
            String srcTable = paramReader.getString("srcTable");
            String dstTable = paramReader.getString("dstTable");
            String srcRow = "", dstRow = "";
            Connection c = null;  // we will re-use this below
            try {
                c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM "+srcTable+" LIMIT 1;");
                if (rs.next()) {
                    srcRow = rs.getString(2);
                }
            } catch (Exception e) {
                throw new UdfException(123, e.getMessage(), e);
            }
            long cnt=0;
            do {
                long a = input_reader.getLong(0);
                long b = input_reader.getLong(1);
                long d = input_reader.getLong(2);

                output_writer.setLong(0, a+b+d);
                output_writer.setLong(1, a*b*d);
                output_writer.setString(2, ""+a+":"+b+":"+d);
                try {
                    PreparedStatement ps = c.prepareStatement("INSERT INTO " + dstTable + " (v1) VALUES (?);");
                    ps.setString(1, srcRow + ":" + a + ":" + b + ":" + d);
                    ps.execute();
                } catch (Exception e) {
                    throw new UdfException(123, e.getMessage(), e);
                }
                output_writer.next();
                cnt++;
            } while (input_reader.next());
        }
    }
}
