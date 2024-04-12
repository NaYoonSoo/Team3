package com.example.moduroad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moduroad.com.example.moduroad.network.SearchItem
import com.example.moduroad.com.example.moduroad.network.SearchViewModel

// ViewModel 클래스를 여기에 추가하거나 별도의 파일로 분리해야 합니다.
// SearchItem 데이터 클래스도 필요합니다.

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        val searchView: SearchView = findViewById(R.id.search_view)
        val noResultsText: TextView = findViewById(R.id.text_no_results)
        val resultsRecyclerView: RecyclerView = findViewById(R.id.results_recycler_view)
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SearchResultsAdapter(emptyList())
        resultsRecyclerView.adapter = adapter

        viewModel.searchResults.observe(this, { items ->
            if (items.isNotEmpty()) {
                adapter.updateData(items)
                noResultsText.visibility = View.GONE
                resultsRecyclerView.visibility = View.VISIBLE
            } else {
                noResultsText.visibility = View.VISIBLE
                resultsRecyclerView.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this, { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchPlaces(it) }
                searchView.clearFocus() // 검색 후 키보드 숨기기
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 여기서 실시간 검색 기능을 구현할 수 있습니다.
                return false
            }
        })

        // 인텐트로부터 전달받은 검색어로 검색 실행
        intent.getStringExtra("query")?.also { query ->
            searchView.setQuery(query, false)
            viewModel.searchPlaces(query)
        }
    }
}

class SearchResultsAdapter(private var results: List<SearchItem>) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int = results.size

    fun updateData(newResults: List<SearchItem>) {
        results = newResults
        notifyDataSetChanged()
    }

    class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.search_item_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.search_item_description)

        fun bind(searchItem: SearchItem) {
            titleTextView.text = searchItem.title
            descriptionTextView.text = searchItem.description
        }
    }
}