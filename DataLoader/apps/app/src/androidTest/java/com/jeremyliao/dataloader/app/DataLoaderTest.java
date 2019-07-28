package com.jeremyliao.dataloader.app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.jeremyliao.dataloader.DataLoader;
import com.jeremyliao.dataloader.app.wrapper.Wrapper;
import com.jeremyliao.dataloader.interfaces.LoadListener;
import com.jeremyliao.dataloader.interfaces.LoadTask;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class DataLoaderTest {

    @Rule
    public ActivityTestRule<TestActivity> rule = new ActivityTestRule<>(TestActivity.class);

    @Test
    public void testContext() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.jeremyliao.dataloader.app", appContext.getPackageName());
    }

    @Test
    public void testLoadData1() {
        final Wrapper<String> result = new Wrapper<>(null);
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(1000);
                return "hello";
            }
        });
        rule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataLoader.listen(id, rule.getActivity(), new LoadListener<String>() {
                    @Override
                    public void onDataArrived(String data) {
                        result.setTarget(data);
                    }
                });
            }
        });
        sleep(2000);
        Assert.assertEquals(result.getTarget(), "hello");
    }

    @Test
    public void testLoadData2() {
        final Wrapper<String> result = new Wrapper<>(null);
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(1000);
                return "hello";
            }
        });
        DataLoader.listen(id, rule.getActivity(), new LoadListener<String>() {
            @Override
            public void onDataArrived(String data) {
                result.setTarget(data);
            }
        });
        sleep(2000);
        Assert.assertEquals(result.getTarget(), "hello");
    }

    @Test
    public void testLoadData3() {
        final Wrapper<String> result = new Wrapper<>(null);
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(1000);
                return "hello";
            }
        }, new LoadListener<String>() {
            @Override
            public void onDataArrived(String data) {
                result.setTarget(data);
            }
        });
        sleep(2000);
        Assert.assertEquals(result.getTarget(), "hello");
    }

    @Test
    public void testLoadData4() {
        final Wrapper<String> result = new Wrapper<>(null);
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(1000);
                return "hello";
            }
        });
        LoadListener<String> listener = new LoadListener<String>() {
            @Override
            public void onDataArrived(String data) {
                result.setTarget(data);
            }
        };
        DataLoader.listen(id, listener);
        sleep(2000);
        DataLoader.removeListener(id, listener);
        Assert.assertEquals(result.getTarget(), "hello");
    }

    @Test
    public void testLoadData5() {
        final Wrapper<String> result = new Wrapper<>(null);
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(500);
                return "hello";
            }
        });
        sleep(1000);
        DataLoader.listen(id, rule.getActivity(), new LoadListener<String>() {
            @Override
            public void onDataArrived(String data) {
                result.setTarget(data);
            }
        });
        sleep(1000);
        Assert.assertEquals(result.getTarget(), "hello");
    }

    @Test
    public void testLoadData6() {
        final Wrapper<String> result = new Wrapper<>(null);
        final int id = DataLoader.load(rule.getActivity(),
                new LoadTask<String>() {
                    @Override
                    public String loadData() {
                        sleep(1000);
                        return "hello";
                    }
                }, new LoadListener<String>() {
                    @Override
                    public void onDataArrived(String data) {
                        result.setTarget(data);
                    }
                });
        sleep(2000);
        Assert.assertEquals(result.getTarget(), "hello");
    }

    @Test
    public void testExist() {
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(1000);
                return "hello";
            }
        });
        Assert.assertTrue(DataLoader.exists(id));
    }

    @Test
    public void testRefresh() {
        final Wrapper<Integer> result = new Wrapper<>(0);
        final int id = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                sleep(500);
                return "hello";
            }
        });
        DataLoader.listen(id, rule.getActivity(), new LoadListener<String>() {
            @Override
            public void onDataArrived(String data) {
                result.setTarget(result.getTarget() + 1);
            }
        });
        sleep(500);
        DataLoader.refresh(id);
        sleep(2000);
        Assert.assertEquals(result.getTarget().intValue(), 2);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
