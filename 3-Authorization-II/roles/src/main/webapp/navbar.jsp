<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="/">Microsoft Identity Platform</a>
    <span class="navbar-text">Authorization II: Use Active Directory Spring Boot Starter to control access by app roles</span>
    <div class="btn-group ml-auto dropleft">
        <ul class="nav navbar-nav navbar-right">
            <sec:authorize access="isAuthenticated()">
                <li class="nav-item">
                    <a class="nav-link" href="/token_details">Hello
                        <sec:authentication property="name" />!
                    </a>
                </li>
                <li>
                    <form:form action="/logout" method="POST">
                        <input class="btn btn-warning" type="submit" value="Sign Out" />
                    </form:form>
                </li>
            </sec:authorize>
            <sec:authorize access="isAnonymous()">
                <li>
                    <a class="btn btn-success" href="/oauth2/authorization/azure">Sign In</a>
                </li>
            </sec:authorize>
        </ul>
    </div>
</nav>