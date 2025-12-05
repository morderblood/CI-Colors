git rev-list --objects --all | sort -k 2 | while read sha name; do 
  size=$(git cat-file -s $sha); 
  if [ $size -gt 50000000 ]; then 
    echo "$((size/1024/1024)) MB  $name"; 
  fi; 
done
