package online.mushu.server.Vo;


import com.example.wx.Common.Util.JSONUtils;

/**
    =======
    =======向前端回复登陆情况的类
*/

enum LICoda {

    Success(200),
    Failure(201),
    FailureRequest(401);


    private final int coda;

    LICoda(int coda) {
        this.coda = coda;
    }

    public int getCoda() {
        return coda;
    }


}

public record Login(String username, int ID, int coda, String JWT) {

    public String asJsonString() {
        return JSONUtils.toJSON(this);
    }

    public static Login Success(String username, int ID, String JWT) {
        return new Login(username, ID, LICoda.Success.getCoda() , JWT);
    }

    public static Login Failure() {
        return new Login("", 0, LICoda.Failure.getCoda(), "");
    }

    public static Login FailureRequest(String username, int ID) {
        return new Login(username, ID, LICoda.FailureRequest.getCoda(), null);
    }
}
