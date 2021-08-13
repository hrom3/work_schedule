
public class Users {

    private Long id;

    private String name;

    private String surname;

    private List<Users> friends;
}

public class FindUserFriends {

    public  List<Users> findFriends(Users user, int level) {

        ArrayList<Users> friendsUser = new ArrayList<>();

        friendsOfUser(user, level, new HashSet<>(), friendsUser);

        return friendsUser;

    }

    public void friendsOfUser(Users user, int level, Set<Users> addedUser,
                                     List<Users> friendsUser) {

        addedUser.add(user);

        if (level <= 0) {
            return;
        }

        level--;

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return;
        }

        for (Users user_tmp : user.getFriends()) {
            if (!addedUser.contains(user_tmp)) {
                friendsUser.add(user_tmp);
            }
            friendsOfUser(user_tmp, level, addedUser, friendsUser);
        }
    }
 }