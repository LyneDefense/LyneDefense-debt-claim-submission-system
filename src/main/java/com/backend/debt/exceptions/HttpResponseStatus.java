package com.backend.debt.exceptions;

/**
 * HTTP响应状态码常量类
 * <p>
 * 定义了标准的HTTP响应状态码，用于API响应中的状态码和原因短语。
 * 该类提供了常用的HTTP状态码常量，避免在代码中直接使用数字状态码，提高代码可读性。
 * </p>
 */
public class HttpResponseStatus {
  // 1xx 信息性状态码
  public static final HttpResponseStatus CONTINUE = newStatus(100, "Continue");
  public static final HttpResponseStatus SWITCHING_PROTOCOLS =
      newStatus(101, "Switching Protocols");
  public static final HttpResponseStatus PROCESSING = newStatus(102, "Processing");
  
  // 2xx 成功状态码
  public static final HttpResponseStatus OK = newStatus(200, "OK");
  public static final HttpResponseStatus CREATED = newStatus(201, "Created");
  public static final HttpResponseStatus ACCEPTED = newStatus(202, "Accepted");
  public static final HttpResponseStatus NON_AUTHORITATIVE_INFORMATION =
      newStatus(203, "Non-Authoritative Information");
  public static final HttpResponseStatus NO_CONTENT = newStatus(204, "No Content");
  public static final HttpResponseStatus RESET_CONTENT = newStatus(205, "Reset Content");
  public static final HttpResponseStatus PARTIAL_CONTENT = newStatus(206, "Partial Content");
  public static final HttpResponseStatus MULTI_STATUS = newStatus(207, "Multi-Status");
  
  // 3xx 重定向状态码
  public static final HttpResponseStatus MULTIPLE_CHOICES = newStatus(300, "Multiple Choices");
  public static final HttpResponseStatus MOVED_PERMANENTLY = newStatus(301, "Moved Permanently");
  public static final HttpResponseStatus FOUND = newStatus(302, "Found");
  public static final HttpResponseStatus SEE_OTHER = newStatus(303, "See Other");
  public static final HttpResponseStatus NOT_MODIFIED = newStatus(304, "Not Modified");
  public static final HttpResponseStatus USE_PROXY = newStatus(305, "Use Proxy");
  public static final HttpResponseStatus TEMPORARY_REDIRECT = newStatus(307, "Temporary Redirect");
  public static final HttpResponseStatus PERMANENT_REDIRECT = newStatus(308, "Permanent Redirect");
  
  // 4xx 客户端错误状态码
  public static final HttpResponseStatus BAD_REQUEST = newStatus(400, "Bad Request");
  public static final HttpResponseStatus UNAUTHORIZED = newStatus(401, "Unauthorized");
  public static final HttpResponseStatus PAYMENT_REQUIRED = newStatus(402, "Payment Required");
  public static final HttpResponseStatus FORBIDDEN = newStatus(403, "Forbidden");
  public static final HttpResponseStatus NOT_FOUND = newStatus(404, "Not Found");
  public static final HttpResponseStatus METHOD_NOT_ALLOWED = newStatus(405, "Method Not Allowed");
  public static final HttpResponseStatus NOT_ACCEPTABLE = newStatus(406, "Not Acceptable");
  public static final HttpResponseStatus PROXY_AUTHENTICATION_REQUIRED =
      newStatus(407, "Proxy Authentication Required");
  public static final HttpResponseStatus REQUEST_TIMEOUT = newStatus(408, "Request Timeout");
  public static final HttpResponseStatus CONFLICT = newStatus(409, "Conflict");
  public static final HttpResponseStatus GONE = newStatus(410, "Gone");
  public static final HttpResponseStatus LENGTH_REQUIRED = newStatus(411, "Length Required");
  public static final HttpResponseStatus PRECONDITION_FAILED =
      newStatus(412, "Precondition Failed");
  public static final HttpResponseStatus REQUEST_ENTITY_TOO_LARGE =
      newStatus(413, "Request Entity Too Large");
  public static final HttpResponseStatus REQUEST_URI_TOO_LONG =
      newStatus(414, "Request-URI Too Long");
  public static final HttpResponseStatus UNSUPPORTED_MEDIA_TYPE =
      newStatus(415, "Unsupported Media Type");
  public static final HttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE =
      newStatus(416, "Requested Range Not Satisfiable");
  public static final HttpResponseStatus EXPECTATION_FAILED = newStatus(417, "Expectation Failed");
  public static final HttpResponseStatus MISDIRECTED_REQUEST =
      newStatus(421, "Misdirected Request");
  public static final HttpResponseStatus UNPROCESSABLE_ENTITY =
      newStatus(422, "Unprocessable Entity");
  public static final HttpResponseStatus LOCKED = newStatus(423, "Locked");
  public static final HttpResponseStatus FAILED_DEPENDENCY = newStatus(424, "Failed Dependency");
  public static final HttpResponseStatus UNORDERED_COLLECTION =
      newStatus(425, "Unordered Collection");
  public static final HttpResponseStatus UPGRADE_REQUIRED = newStatus(426, "Upgrade Required");
  public static final HttpResponseStatus PRECONDITION_REQUIRED =
      newStatus(428, "Precondition Required");
  public static final HttpResponseStatus TOO_MANY_REQUESTS = newStatus(429, "Too Many Requests");
  public static final HttpResponseStatus REQUEST_HEADER_FIELDS_TOO_LARGE =
      newStatus(431, "Request Header Fields Too Large");
  
  // 5xx 服务器错误状态码
  public static final HttpResponseStatus INTERNAL_SERVER_ERROR =
      newStatus(500, "Internal Server Error");
  public static final HttpResponseStatus NOT_IMPLEMENTED = newStatus(501, "Not Implemented");
  public static final HttpResponseStatus BAD_GATEWAY = newStatus(502, "Bad Gateway");
  public static final HttpResponseStatus SERVICE_UNAVAILABLE =
      newStatus(503, "Service Unavailable");
  public static final HttpResponseStatus GATEWAY_TIMEOUT = newStatus(504, "Gateway Timeout");
  public static final HttpResponseStatus HTTP_VERSION_NOT_SUPPORTED =
      newStatus(505, "HTTP Version Not Supported");
  public static final HttpResponseStatus VARIANT_ALSO_NEGOTIATES =
      newStatus(506, "Variant Also Negotiates");
  public static final HttpResponseStatus INSUFFICIENT_STORAGE =
      newStatus(507, "Insufficient Storage");
  public static final HttpResponseStatus NOT_EXTENDED = newStatus(510, "Not Extended");
  public static final HttpResponseStatus NETWORK_AUTHENTICATION_REQUIRED =
      newStatus(511, "Network Authentication Required");
      
  /**
   * HTTP状态码
   */
  private final int code;
  
  /**
   * HTTP状态描述
   */
  private final String reasonPhrase;

  /**
   * 创建新的HTTP响应状态实例
   *
   * @param statusCode 状态码
   * @param reasonPhrase 状态描述
   * @return 新的HTTP响应状态实例
   */
  private static HttpResponseStatus newStatus(int statusCode, String reasonPhrase) {
    return new HttpResponseStatus(statusCode, reasonPhrase);
  }

  /**
   * 构造函数
   *
   * @param code 状态码
   * @param reasonPhrase 状态描述
   */
  public HttpResponseStatus(int code, String reasonPhrase) {
    this.code = code;
    this.reasonPhrase = reasonPhrase;
  }

  /**
   * 获取状态码
   *
   * @return 状态码
   */
  public int code() {
    return this.code;
  }

  /**
   * 获取状态描述
   *
   * @return 状态描述
   */
  public String reasonPhrase() {
    return this.reasonPhrase;
  }
}
