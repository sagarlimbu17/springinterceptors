**Q.How does the DispatcherServlet, Resolver and Controllers interact?**

When sending a request to your application the following happens:

1. The request arrives at your server (e.g. Tomcat). Depending on the context path in the url the server decides to which application the request belongs.

2. Depending on the url and the servlet mapping in the web.xml file of your application the server knows which servlet should handle the request.

3. The request is passed to the servlet filter chain which can modify or reject requests
The servlet takes control over the request. In case of your Spring application the spring Dispatcherservlet receives the request. Now Spring kicks in.

4. The request is processed by mvc intercepters preHandle methods

5. The request is mapped to a controller based on the url. The corresponding controller method will be called.

6. Your controller is processing the request. Many different responses can be returned in controllers (jsp, pdf, json, redirects, etc.). For now i assume you want to render a simple jsp view. Result of the controller are two things: a model and a view. The model is a map that contains the data you want to access later in your view. The view at this stage is most of the time a simple string containing a view name.

7. Registered springs mvc interceptors can kick in again using the postHandle method (e.g. for modifying the model)

8. The 'view' result of your controller is resolved to a real View using a ViewResolver. Depending on the ViewResolver the result can be jsp page, a tiles view, a thymeleaf template or many other 'Views'. In your case the ViewResolver resolves a view name (e.g. 'myPage') to a jsp file (e.g. /WEB-INF/jsp/myPage.jsp)

9. The view is rendered using the model data returned by your controller

10. The response with the rendered view will be passed to mvc interceptors again (afterCompletion method)

11. The response leaves the dispatcher servlet. Here ends spring land.

12. The response passes servlet filters again

13. The response is send back to client


## Intercepting the HTTP request: 

    public class RequestProcessingTimeInterceptors extends HandlerInterceptorAdapter {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RequestProcessingTimeInterceptors.class);
	
	/*
	 * This method is used to intercept the request before it’s handed over to the
	 * handler method. This method should return ‘true’ to let Spring know to
	 * process the request through another spring intercepter or to send it to
	 * handler method if there are no further spring interceptors.If this method
	 * returns ‘false’ Spring framework assumes that request has been handled by the
	 * spring intercepter itself and no further processing is needed. We should use
	 * response object to send response to the client request in this case.
	 * 
	 * Object handler is the chosen handler object to handle the request. This
	 * method can throw Exception also, in that case Spring MVC Exception Handling
	 * should be useful to send error page as response.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) {
		long startTime = System.currentTimeMillis();
		logger.info("Request URL "+ request.getRequestURI().toString() + ":: Start time= "+System.currentTimeMillis());
		request.setAttribute("startTime", startTime);
		return true;
	}
	
	/*
	 * This HandlerInterceptor interceptor method is called when HandlerAdapter has
	 * invoked the handler but DispatcherServlet is yet to render the view. This
	 * method can be used to add additional attribute to the ModelAndView object to
	 * be used in the view pages. We can use this spring interceptor method to
	 * determine the time taken by handler method to process the client request.
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("Request URL::" + request.getRequestURL().toString()
				+ " Sent to Handler :: Current Time=" + System.currentTimeMillis());
		//we can add attributes in the modelAndView and use that in the view page
	}
	
	/*
	 * This is a HandlerInterceptor callback method that is called once the handler
	 * is executed and view is rendered.
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		logger.info("Request URL::" + request.getRequestURL().toString()
				+ ":: End Time=" + System.currentTimeMillis());
		logger.info("Request URL::" + request.getRequestURL().toString()
				+ ":: Time Taken=" + (System.currentTimeMillis() - startTime));
	}
	
	/*
	 * If there are multiple spring interceptors configured, preHandle() method is
	 * executed in the order of configuration whereas postHandle() and
	 * afterCompletion() methods are invoked in the reverse order.
	 */

    }

