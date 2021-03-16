<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="/">Microsoft Identity Platform</a>
    <span class="navbar-text">Authentication: Use Active Directory Spring Boot Starter to sign in users</span>
    <div class="btn-group ml-auto dropleft">
        <ul class="nav navbar-nav navbar-right">
            <sec:authorize access="isAuthenticated()">
                <!-- could have also tested if access equals "!hasRole('ROLE_ANONYMOUS') -->
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
                <!-- could have also tested if access equals "hasRole('ROLE_ANONYMOUS')" -->
                <li>
                    <a class="btn btn-success" href="/oauth2/authorization/azure">Sign In</a>
                </li>
            </sec:authorize>
        </ul>
    </div>
</nav>