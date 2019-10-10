package top.whiteyang.br.common.facade;

/**
 * │＼＿＿╭╭╭╭╭＿＿／│
 * │　　　　　　　　　│
 * │　　　　　　　　　│
 * │　－　　　　　　－│
 * │≡　　　　ｏ　≡   │
 * │　　　　　　　　　│
 * ╰——┬Ｏ◤▽◥Ｏ┬———╯
 * ｜　　ｏ　　｜
 * ｜╭－－－－╮｜
 * Created by IntelliJ IDEA.
 *
 * @author : whiteyang
 * @email: yangyuanjian@souche.com
 * @time: 2019/10/10 3:38 下午
 */
public interface DataProcessor<Input,Output> {
    Output process(Input input);
}
