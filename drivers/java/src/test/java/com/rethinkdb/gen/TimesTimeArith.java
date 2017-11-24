// Autogenerated by convert_tests.py and process_polyglot.py.
// Do not edit this file directly.
// The template for this file is located at:
// ../../../../../../../templates/Test.java
package com.rethinkdb.gen;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.TestingFramework;
import com.rethinkdb.gen.ast.EpochTime;
import com.rethinkdb.gen.ast.MakeArray;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.rethinkdb.TestingCommon.*;
import static org.junit.Assert.assertEquals;

public class TimesTimeArith {
    // Test basic time arithmetic
    Logger logger = LoggerFactory.getLogger(TimesTimeArith.class);
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

        // times/time_arith.yaml line #3
        // rt1 = 1375147296.681
        logger.info("Possibly executing: Double rt1 = (Double) (1375147296.681);");
        Double rt1 = (Double) (1375147296.681);

        // times/time_arith.yaml line #4
        // rt2 = 1375147296.682
        logger.info("Possibly executing: Double rt2 = (Double) (1375147296.682);");
        Double rt2 = (Double) (1375147296.682);

        // times/time_arith.yaml line #5
        // rt3 = 1375147297.681
        logger.info("Possibly executing: Double rt3 = (Double) (1375147297.681);");
        Double rt3 = (Double) (1375147297.681);

        // times/time_arith.yaml line #6
        // rt4 = 2375147296.681
        logger.info("Possibly executing: Double rt4 = (Double) (2375147296.681);");
        Double rt4 = (Double) (2375147296.681);

        // times/time_arith.yaml line #7
        // rts = [rt1, rt2, rt3, rt4]
        logger.info("Possibly executing: List rts = (List) (r.array(rt1, rt2, rt3, rt4));");
        List rts = (List) (r.array(rt1, rt2, rt3, rt4));

        // times/time_arith.yaml line #9
        // t1 = r.epoch_time(rt1)
        logger.info("Possibly executing: EpochTime t1 = (EpochTime) (r.epochTime(rt1));");
        EpochTime t1 = (EpochTime) (r.epochTime(rt1));

        // times/time_arith.yaml line #10
        // t2 = r.epoch_time(rt2)
        logger.info("Possibly executing: EpochTime t2 = (EpochTime) (r.epochTime(rt2));");
        EpochTime t2 = (EpochTime) (r.epochTime(rt2));

        // times/time_arith.yaml line #11
        // t3 = r.epoch_time(rt3)
        logger.info("Possibly executing: EpochTime t3 = (EpochTime) (r.epochTime(rt3));");
        EpochTime t3 = (EpochTime) (r.epochTime(rt3));

        // times/time_arith.yaml line #12
        // t4 = r.epoch_time(rt4)
        logger.info("Possibly executing: EpochTime t4 = (EpochTime) (r.epochTime(rt4));");
        EpochTime t4 = (EpochTime) (r.epochTime(rt4));

        // times/time_arith.yaml line #13
        // ts = r.expr([t1, t2, t3, t4])
        logger.info("Possibly executing: MakeArray ts = (MakeArray) (r.expr(r.array(t1, t2, t3, t4)));");
        MakeArray ts = (MakeArray) (r.expr(r.array(t1, t2, t3, t4)));

        {
            // times/time_arith.yaml line #17
            /* true */
            Boolean expected_ = true;
            /* ((t2 - t1) * 1000).do(lambda x:(x > 0.99) & (x < 1.01)) */
            logger.info("About to run line #17: r.sub(t2, t1).mul(1000L).do_(x -> r.gt(x, 0.99).and(r.lt(x, 1.01)))");
            Object obtained = runOrCatch(r.sub(t2, t1).mul(1000L).do_(x -> r.gt(x, 0.99).and(r.lt(x, 1.01))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #17");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #17:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #20
            /* 1 */
            Long expected_ = 1L;
            /* t3 - t1 */
            logger.info("About to run line #20: r.sub(t3, t1)");
            Object obtained = runOrCatch(r.sub(t3, t1),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #20");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #20:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #23
            /* 1000000000 */
            Long expected_ = 1000000000L;
            /* t4 - t1 */
            logger.info("About to run line #23: r.sub(t4, t1)");
            Object obtained = runOrCatch(r.sub(t4, t1),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #23");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #23:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #28
            /* true */
            Boolean expected_ = true;
            /* ((t1 - t2) * 1000).do(lambda x:(x < -0.99) & (x > -1.01)) */
            logger.info("About to run line #28: r.sub(t1, t2).mul(1000L).do_(x -> r.lt(x, -0.99).and(r.gt(x, -1.01)))");
            Object obtained = runOrCatch(r.sub(t1, t2).mul(1000L).do_(x -> r.lt(x, -0.99).and(r.gt(x, -1.01))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #28");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #28:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #31
            /* -1 */
            Long expected_ = -1L;
            /* t1 - t3 */
            logger.info("About to run line #31: r.sub(t1, t3)");
            Object obtained = runOrCatch(r.sub(t1, t3),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #31");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #31:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #34
            /* -1000000000 */
            Long expected_ = -1000000000L;
            /* t1 - t4 */
            logger.info("About to run line #34: r.sub(t1, t4)");
            Object obtained = runOrCatch(r.sub(t1, t4),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #34");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #34:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #39
            /* ([rt1, rt2, rt3, rt4]) */
            List expected_ = r.array(rt1, rt2, rt3, rt4);
            /* ts.map(lambda x:t1 + (x - t1)).map(lambda x:x.to_epoch_time()) */
            logger.info("About to run line #39: ts.map(x -> r.add(t1, r.sub(x, t1))).map(x -> x.toEpochTime())");
            Object obtained = runOrCatch(ts.map(x -> r.add(t1, r.sub(x, t1))).map(x -> x.toEpochTime()),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #39");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #39:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #43
            /* err("ReqlQueryLogicError", "Expected type NUMBER but found PTYPE<TIME>.", []) */
            Err expected_ = err("ReqlQueryLogicError", "Expected type NUMBER but found PTYPE<TIME>.", r.array());
            /* ts.map(lambda x:(t1 + x) - t1).map(lambda x:x.to_epoch_time()) */
            logger.info("About to run line #43: ts.map(x -> r.add(t1, x).sub(t1)).map(x -> x.toEpochTime())");
            Object obtained = runOrCatch(ts.map(x -> r.add(t1, x).sub(t1)).map(x -> x.toEpochTime()),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #43");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #43:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #47
            /* ([rt1, rt2, rt3, rt4]) */
            List expected_ = r.array(rt1, rt2, rt3, rt4);
            /* ts.map(lambda x:t1 - (t1 - x)).map(lambda x:x.to_epoch_time()) */
            logger.info("About to run line #47: ts.map(x -> r.sub(t1, r.sub(t1, x))).map(x -> x.toEpochTime())");
            Object obtained = runOrCatch(ts.map(x -> r.sub(t1, r.sub(t1, x))).map(x -> x.toEpochTime()),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #47");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #47:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #52
            /* ([[[false, true,  true,  false, true,  false],
[true,  true,  false, true,  false, false],
[true,  true,  false, true,  false, false],
[true,  true,  false, true,  false, false]],
[[false, false, false, true,  true,  true],
[false, true,  true,  false, true,  false],
[true,  true,  false, true,  false, false],
[true,  true,  false, true,  false, false]],
[[false, false, false, true,  true,  true],
[false, false, false, true,  true,  true],
[false, true,  true,  false, true,  false],
[true,  true,  false, true,  false, false]],
[[false, false, false, true,  true,  true],
[false, false, false, true,  true,  true],
[false, false, false, true,  true,  true],
[false, true,  true,  false, true,  false]]]) */
            List expected_ = r.array(r.array(r.array(false, true, true, false, true, false), r.array(true, true, false, true, false, false), r.array(true, true, false, true, false, false), r.array(true, true, false, true, false, false)), r.array(r.array(false, false, false, true, true, true), r.array(false, true, true, false, true, false), r.array(true, true, false, true, false, false), r.array(true, true, false, true, false, false)), r.array(r.array(false, false, false, true, true, true), r.array(false, false, false, true, true, true), r.array(false, true, true, false, true, false), r.array(true, true, false, true, false, false)), r.array(r.array(false, false, false, true, true, true), r.array(false, false, false, true, true, true), r.array(false, false, false, true, true, true), r.array(false, true, true, false, true, false)));
            /* ts.map(lambda x:ts.map(lambda y:[x < y, x <= y, x == y, x != y, x >= y, x > y])) */
            logger.info("About to run line #52: ts.map(x -> ts.map(y -> r.array(r.lt(x, y), r.le(x, y), r.eq(x, y), r.ne(x, y), r.ge(x, y), r.gt(x, y))))");
            Object obtained = runOrCatch(ts.map(x -> ts.map(y -> r.array(r.lt(x, y), r.le(x, y), r.eq(x, y), r.ne(x, y), r.ge(x, y), r.gt(x, y)))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #52");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #52:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        // times/time_arith.yaml line #73
        // datum_types = r.expr([null, true, false, 1, "1", [1], {"1":1}, r.binary(b'')])
        logger.info("Possibly executing: MakeArray datum_types = (MakeArray) (r.expr(r.array(null, true, false, 1L, '1', r.array(1L), r.hashMap('1', 1L), r.binary(new byte[]{}))));");
        MakeArray datum_types = (MakeArray) (r.expr(r.array(null, true, false, 1L, "1", r.array(1L), r.hashMap("1", 1L), r.binary(new byte[]{}))));

        {
            // times/time_arith.yaml line #79
            /* ([[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]],
[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]],
[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]],
[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]],
[[false, false, false, true,  true,  true],
[true,  true,  false, true,  false, false]],
[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]],
[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]],
[[true,  true,  false, true,  false, false],
[false, false, false, true,  true,  true]]]) */
            List expected_ = r.array(r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)), r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)), r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)), r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)), r.array(r.array(false, false, false, true, true, true), r.array(true, true, false, true, false, false)), r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)), r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)), r.array(r.array(true, true, false, true, false, false), r.array(false, false, false, true, true, true)));
            /* datum_types.map(lambda x:r.expr([[x, t1], [t1, x]]).map(lambda xy:xy[0].do(lambda x2:xy[1].do(lambda y:[x2 < y, x2 <= y, x2 == y, x2 != y, x2 >= y, x2 > y])))) */
            logger.info("About to run line #79: datum_types.map(x -> r.expr(r.array(r.array(x, t1), r.array(t1, x))).map(xy -> xy.bracket(0L).do_(x2 -> xy.bracket(1L).do_(y -> r.array(r.lt(x2, y), r.le(x2, y), r.eq(x2, y), r.ne(x2, y), r.ge(x2, y), r.gt(x2, y))))))");
            Object obtained = runOrCatch(datum_types.map(x -> r.expr(r.array(r.array(x, t1), r.array(t1, x))).map(xy -> xy.bracket(0L).do_(x2 -> xy.bracket(1L).do_(y -> r.array(r.lt(x2, y), r.le(x2, y), r.eq(x2, y), r.ne(x2, y), r.ge(x2, y), r.gt(x2, y)))))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #79");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #79:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #99
            /* ([[[false, true,  true,  true],
[false, false, true,  true],
[false, false, false, true],
[false, false, false, false]],
[[false, false, false, false],
[false, false, true,  true],
[false, false, false, true],
[false, false, false, false]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, true],
[false, false, false, false]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, false],
[false, false, false, false]]]) */
            List expected_ = r.array(r.array(r.array(false, true, true, true), r.array(false, false, true, true), r.array(false, false, false, true), r.array(false, false, false, false)), r.array(r.array(false, false, false, false), r.array(false, false, true, true), r.array(false, false, false, true), r.array(false, false, false, false)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, true), r.array(false, false, false, false)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false)));
            /* ts.map(lambda a:ts.map(lambda b:ts.map(lambda c:b.during(a, c)))) */
            logger.info("About to run line #99: ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c))))");
            Object obtained = runOrCatch(ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c)))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #99");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #99:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #119
            /* ([[[false, false, false, false],
[false, false, true,  true],
[false, false, false, true],
[false, false, false, false]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, true],
[false, false, false, false]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, false],
[false, false, false, false]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, false],
[false, false, false, false]]]) */
            List expected_ = r.array(r.array(r.array(false, false, false, false), r.array(false, false, true, true), r.array(false, false, false, true), r.array(false, false, false, false)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, true), r.array(false, false, false, false)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false)));
            /* ts.map(lambda a:ts.map(lambda b:ts.map(lambda c:b.during(a, c, left_bound='open')))) */
            logger.info("About to run line #119: ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c).optArg('left_bound', 'open'))))");
            Object obtained = runOrCatch(ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c).optArg("left_bound", "open")))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #119");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #119:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #139
            /* ([[[true,  true,  true,  true],
[false, true,  true,  true],
[false, false, true,  true],
[false, false, false, true]],
[[false, false, false, false],
[false, true,  true,  true],
[false, false, true,  true],
[false, false, false, true]],
[[false, false, false, false],
[false, false, false, false],
[false, false, true,  true],
[false, false, false, true]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, false],
[false, false, false, true]]]) */
            List expected_ = r.array(r.array(r.array(true, true, true, true), r.array(false, true, true, true), r.array(false, false, true, true), r.array(false, false, false, true)), r.array(r.array(false, false, false, false), r.array(false, true, true, true), r.array(false, false, true, true), r.array(false, false, false, true)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, true, true), r.array(false, false, false, true)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, true)));
            /* ts.map(lambda a:ts.map(lambda b:ts.map(lambda c:b.during(a, c, right_bound='closed')))) */
            logger.info("About to run line #139: ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c).optArg('right_bound', 'closed'))))");
            Object obtained = runOrCatch(ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c).optArg("right_bound", "closed")))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #139");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #139:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #159
            /* ([[[false, false, false, false],
[false, true,  true,  true],
[false, false, true,  true],
[false, false, false, true]],
[[false, false, false, false],
[false, false, false, false],
[false, false, true,  true],
[false, false, false, true]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, false],
[false, false, false, true]],
[[false, false, false, false],
[false, false, false, false],
[false, false, false, false],
[false, false, false, false]]]) */
            List expected_ = r.array(r.array(r.array(false, false, false, false), r.array(false, true, true, true), r.array(false, false, true, true), r.array(false, false, false, true)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, true, true), r.array(false, false, false, true)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, true)), r.array(r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false), r.array(false, false, false, false)));
            /* ts.map(lambda a:ts.map(lambda b:ts.map(lambda c:b.during(a, c, left_bound='open', right_bound='closed')))) */
            logger.info("About to run line #159: ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c).optArg('left_bound', 'open').optArg('right_bound', 'closed'))))");
            Object obtained = runOrCatch(ts.map(a -> ts.map(b -> ts.map(c -> b.during(a, c).optArg("left_bound", "open").optArg("right_bound", "closed")))),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #159");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #159:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #179
            /* rts */
            List expected_ = rts;
            /* ts.map(lambda x:x.date() + x.time_of_day()).map(lambda x:x.to_epoch_time()) */
            logger.info("About to run line #179: ts.map(x -> x.date().add(x.timeOfDay())).map(x -> x.toEpochTime())");
            Object obtained = runOrCatch(ts.map(x -> x.date().add(x.timeOfDay())).map(x -> x.toEpochTime()),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #179");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #179:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #185
            /* rt1 */
            Double expected_ = rt1;
            /* r.epoch_time(rt1).do(r.js("(function(data){return data})")).to_epoch_time() */
            logger.info("About to run line #185: r.epochTime(rt1).do_(r.js('(function(data){return data})')).toEpochTime()");
            Object obtained = runOrCatch(r.epochTime(rt1).do_(r.js("(function(data){return data})")).toEpochTime(),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals((double) expected_,
                             ((Number) obtained).doubleValue(),
                             0.00000000001);
            logger.info("Finished running line #185");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #185:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #190
            /* ("2012-08-01T00:00:00+00:00") */
            String expected_ = "2012-08-01T00:00:00+00:00";
            /* r.do(r.js("new Date('2012-08-01')")).to_iso8601() */
            logger.info("About to run line #190: r.do_(r.js('new Date('2012-08-01')')).toIso8601()");
            Object obtained = runOrCatch(r.do_(r.js("new Date('2012-08-01')")).toIso8601(),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #190");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #190:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }

        {
            // times/time_arith.yaml line #195
            /* ("2012-08-01T00:00:00+00:00") */
            String expected_ = "2012-08-01T00:00:00+00:00";
            /* r.do(r.js("(function(x){doc = new Object(); doc.date = new Date('2012-08-01'); return doc;})"))["date"].to_iso8601() */
            logger.info("About to run line #195: r.do_(r.js('(function(x){doc = new Object(); doc.date = new Date('2012-08-01'); return doc;})')).bracket('date').toIso8601()");
            Object obtained = runOrCatch(r.do_(r.js("(function(x){doc = new Object(); doc.date = new Date('2012-08-01'); return doc;})")).bracket("date").toIso8601(),
                                          new OptArgs()
                                          ,conn);
            try {
                assertEquals(expected_, obtained);
            logger.info("Finished running line #195");
            } catch (Throwable ae) {
                logger.error("Whoops, got exception on line #195:" + ae.toString());
                if(obtained instanceof Throwable) {
                    ae.addSuppressed((Throwable) obtained);
                }
                throw ae;
            }
        }
    }
}
