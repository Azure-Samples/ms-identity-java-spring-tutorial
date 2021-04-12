<div class="card">
    <h5 class="card-header bg-primary">
        403: Forbidden
    </h5>
    <div class="card-body">
        <p class="card-text">
            Unfortunately, you are not a member of the security group(s) that are allowed to visit this page.
            <br>
            This page requires all of the following groups: <strong>${groupsRequired}</strong>
            <br><br>
            <a class="btn btn-success" href="/sign_in_status">Sign-in Status</a>
            <a class="btn btn-success" href="/token_details">ID Token Details</a>
            <a class="btn btn-success" href="/token_groups">Token Groups</a>
            <a class="btn btn-success" href="/preauthorize_groups">PreAuthorize Groups</a>
        </p>
    </div>
</div>
