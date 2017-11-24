// Autogenerated by convert_tests.py and process_polyglot.py.
// Do not edit this file directly.
// The template for this file is located at:
// ../../../../../../../templates/Test.java
package com.rethinkdb.gen;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.TestingFramework;
import com.rethinkdb.gen.ast.ReqlExpr;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.rethinkdb.TestingCommon.runOrCatch;
import static org.junit.Assert.assertEquals;

public class DatumTypeof {
    // These tests test the type of command
    Logger logger = LoggerFactory.getLogger(DatumTypeof.class);
    public static final RethinkDB r = RethinkDB.r;

    Connection conn;

    @Before
    public void setUp() throws Exception {
        logger.info("Setting up.");
        conn = TestingFramework.createConnection();
        try {
            r.dbCreate("test").run(conn);
            r.db("test").wait_().run(conn);
        }catch (Exception e){}
    }

    @After
    public void tearDown() throws Exception {
        logger.info("Tearing down.");
        r.db("rethinkdb").table("_debug_scratch").delete().run(conn);
        if(!conn.isOpen()){
            conn.close();
            conn = TestingFramework.createConnection();
        }
        r.dbDrop("test").run(conn);
        conn.close(false);
    }

    // Autogenerated tests below

    @Test(timeout=120000)
    public void test() throws Exception {

        {
            // datum/typeof.yaml line #5
            /* 'NULL' */
            String expected_ = "NULL";
            /* r.expr(null).type_of() */
            logger.info("About to run line #5: r.expr((ReqlExpr) null).typeOf()");
            Object obtained = runOrCatch(r.expr((ReqlExpr) null).typeOf(),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #5");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #5:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // datum/typeof.yaml line #9
            /* 'NULL' */
            String expected_ = "NULL";
            /* r.type_of(null) */
            logger.info("About to run line #9: r.typeOf((ReqlExpr) null)");
            Object obtained = runOrCatch(r.typeOf((ReqlExpr) null),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #9");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #9:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }
    }
}
