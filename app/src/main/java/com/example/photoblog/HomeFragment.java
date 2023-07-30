package com.example.photoblog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private RecyclerView blog_post;
    private List<BlogPost> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BlogRecycleAdapter blogRecycleAdapter;
    private DocumentSnapshot lastVisible;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        blog_list=new ArrayList<>();
        blog_post=view.findViewById(R.id.blog_post);
        firebaseAuth= FirebaseAuth.getInstance();
        blogRecycleAdapter=new BlogRecycleAdapter(blog_list);
        blog_post.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_post.setAdapter(blogRecycleAdapter);
        if(firebaseAuth.getCurrentUser()!=null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
//            blog_post.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    Boolean reachedBottom=!recyclerView.canScrollVertically(-1);
//                    if(reachedBottom)
//                    {
//                        String desc=lastVisible.getString("description");
//                        Toast.makeText(container.getContext(),"Reached: "+desc,Toast.LENGTH_SHORT);
//                        loadMorePost();
//                    }
//                }
//            });
            Query firstQuery=firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING);
//            firstQuery.addSnapshotListener((documentSnapshots,e)->{
//                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);
//                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
//                        blog_list.add(blogPost);
//                        blogRecycleAdapter.notifyDataSetChanged();
//                    }
//
//                }
//
//            });
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(!value.isEmpty()) {
                        lastVisible = value.getDocuments().get(value.size() - 1);

                        for (DocumentChange doc : value.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogPostID=doc.getDocument().getId();

                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                                blog_list.add(blogPost);
                                blogRecycleAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }
            });
        }
            return view;

    }
//    public void loadMorePost()
//    {
//
//        Query nextQuery=firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastVisible);
////            firstQuery.addSnapshotListener((documentSnapshots,e)->{
////                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);
////                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
////                    if (doc.getType() == DocumentChange.Type.ADDED) {
////                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
////                        blog_list.add(blogPost);
////                        blogRecycleAdapter.notifyDataSetChanged();
////                    }
////
////                }
////
////            });
//        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            //lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(!value.isEmpty()) {
//                    lastVisible = value.getDocuments().get(value.size() - 1);
//
//                    for (DocumentChange doc : value.getDocumentChanges()) {
//
//                        if (doc.getType() == DocumentChange.Type.ADDED) {
//                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
//                            blog_list.add(blogPost);
//                            blogRecycleAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                }
//            }
//        });
//    }
}