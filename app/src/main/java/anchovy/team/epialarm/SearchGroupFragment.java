package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.services.GroupsService;

public class SearchGroupFragment extends  SearchFragment<Group, GroupsService, GroupsViewModel> {
    protected SearchGroupFragment() {
        super(Group::getName, R.layout.fragment_search_group, GroupsViewModel.class,
                R.id.groupListView);
    }

    @Override
    protected void setSession(Group selectedItem) {
        session.setChosenType("group");
        session.setGroupId(selectedItem.getId());
        session.setGroupName(selectedItem.getName());
    }

    @Override
    protected GroupsService getService() {
        return  GroupsService.getGroupsService();
    }
}