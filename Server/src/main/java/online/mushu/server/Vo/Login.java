package online.mushu.server.Vo;


import online.mushu.server.Common.Util.JSONUtils;

/**
    =======
    =======向前端回复登陆情况的类
*/

enum LICoda {

    Success(200),
    Failure(201),
    FailureRequest(401);


    private final int code;

    LICoda(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}

public record Login(String username, int ID, int coda, String JWT) {

    public String asJsonString() {
        return JSONUtils.toJSON(this);
    }

    public static Login Success(String username, int ID, String JWT) {
        return new Login(username, ID, LICoda.Success.getCode() , JWT);
    }

    public static Login Failure() {
        return new Login("", 0, LICoda.Failure.getCode(), "");
    }

    public static Login FailureRequest(String username, int ID) {
        return new Login(username, ID, LICoda.FailureRequest.getCode(), null);
    }
}
