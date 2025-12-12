package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Teacher;
import anchovy.team.epialarm.zeus.services.TeacherService;

public class SearchTeacherFragment extends SearchFragment<Teacher,
        TeacherService, TeacherViewModel> {
    protected SearchTeacherFragment() {
        super(Teacher::getName, R.layout.fragment_search_teacher, TeacherViewModel.class,
                R.id.groupListView);
    }

    @Override
    protected void setSession(Teacher selectedItem) {
        session.setChosenType("teacher");
        session.setTeacherName(selectedItem.getName());
        session.setTeacherId(selectedItem.getId());
    }

    @Override
    protected TeacherService getService() {
        return TeacherService.getTeacherServiceInstance();
    }
}